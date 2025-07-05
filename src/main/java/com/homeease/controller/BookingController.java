
package com.homeease.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.web.exchanges.HttpExchange.Principal;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.homeease.config.EmailService;
import com.homeease.entity.Booking;
import com.homeease.entity.Review;
import com.homeease.entity.User;
import com.homeease.repository.BookingRepository;
import com.homeease.repository.UserRepository;
import com.homeease.service.BookingService;
import com.homeease.service.ReviewService;
import com.homeease.util.PdfInvoiceGenerator;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import jakarta.transaction.Transactional;


@Controller
public class BookingController {

    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private BookingService bookingService;
    
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ReviewService reviewService;
/*
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/my-bookings")
    public String myBookings(Model model, Principal principal) {
        User user = userRepository.findByEmail(principal.getName());
        List<Booking> bookings = bookingRepository.findByUser(user);
        model.addAttribute("bookings", bookings);
        return "booking";
    }
    
    
    */
	@Autowired
	private EmailService emailService;

	public void confirmBooking(Booking booking) {
	    Booking savedBooking = bookingRepository.save(booking);

	    try {
	        
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();

	        
	        Document document = new Document();
	        PdfWriter.getInstance(document, baos);
	        document.open();

	        
	        document.add(new Paragraph("Booking Invoice"));
	        document.add(new Paragraph("Booking ID: " + savedBooking.getId()));
	        document.add(new Paragraph("Customer Name: " + savedBooking.getCustomerName()));
	        document.add(new Paragraph("Service: " + savedBooking.getService().getTitle()));
	        document.add(new Paragraph("Address: " + savedBooking.getAddress()));
	        document.add(new Paragraph("Total Amount: " + savedBooking.getTotalAmount()));
	        Image logo = Image.getInstance("src/main/resources/static/images/logo.png");
            logo.scaleToFit(100, 50);  
            document.add(logo);
	      

	        document.close();

	        byte[] pdfBytes = baos.toByteArray();

	        
	        emailService.sendBookingInvoice(
	            savedBooking.getUser().getEmail(),
	            "Your Booking Invoice - HomeEase",
	            "<p>Dear " + savedBooking.getCustomerName() + ",</p>" +
	            "<p>Thank you for booking with HomeEase! Please find your invoice attached.</p>",
	            pdfBytes,
	            "invoice_" + savedBooking.getId() + ".pdf"
	        );

	        
	        emailService.sendBookingConfirmation(
	            savedBooking.getUser().getEmail(),
	            savedBooking.getUser().getName(),
	            savedBooking.getId().intValue(),
	            savedBooking.getService().getTitle()
	        );

	    } catch (Exception e) {
	        
	        e.printStackTrace();
	    }
	}


	@PostMapping("/bookings")
	public String createBooking(@ModelAttribute Booking booking) {
	    bookingService.confirmBooking(booking);
	    return "redirect:/bookings/success";
	}



	@PostMapping("/admin/update-booking-status")
	public String updateBookingStatus(@RequestParam int bookingId,
	                                  @RequestParam String status,
	                                  RedirectAttributes redirectAttributes) {

	    Booking booking = bookingRepository.findById(bookingId).orElse(null);

	    if (booking != null) {
	        booking.setStatus(status);
	        bookingRepository.save(booking);

	        
	        String subject = "HomeEase Booking Status Updated";
	        String content = "<h2>Hello " + booking.getUser().getName() + ",</h2>" +
	                         "<p>Your booking with ID <strong>" + booking.getId() + "</strong> has been updated.</p>" +
	                         "<p>New Status: <strong>" + status + "</strong></p>" +
	                         "<br><p>Thank you for using HomeEase!</p>";

	        emailService.sendHtmlEmail(booking.getUser().getEmail(), subject, content);

	        redirectAttributes.addFlashAttribute("msg", "Status updated and email sent.");
	    } else {
	        redirectAttributes.addFlashAttribute("error", "Booking not found.");
	    }

	    return "redirect:/admin/bookings";  
	}
	
	
	@GetMapping("/download-invoice")
	public ResponseEntity<InputStreamResource> downloadInvoice(@RequestParam("bookingId") int bookingId) {
	    Booking booking = bookingRepository.findById(bookingId).orElse(null);
	    if (booking == null) {
	        return ResponseEntity.notFound().build();
	    }
	   

	    ByteArrayInputStream bis = PdfInvoiceGenerator.generateInvoice(booking);

	    HttpHeaders headers = new HttpHeaders();
	    headers.add("Content-Disposition", "inline; filename=invoice_" + bookingId + ".pdf");

	    return ResponseEntity
	            .ok()
	            .headers(headers)
	            .contentType(MediaType.APPLICATION_PDF)
	            .body(new InputStreamResource(bis));
	}
	
	@GetMapping("/user/track-booking")
    public String trackBooking(
            @AuthenticationPrincipal UserDetails loggedUser,
            @RequestParam("bookingId") int bookingId,
            Model model) {

        
        User user = userRepository.findByEmail(loggedUser.getUsername());
        if (user == null) {
            return "redirect:/login";
        }

        
        Booking booking = bookingRepository.findById(bookingId).orElse(null);
        if (booking == null || !booking.getUser().getId().equals(user.getId())) {
            model.addAttribute("errorMessage", "Booking not found or access denied.");
            return "user/track_booking";
        }

      
        model.addAttribute("booking", booking);
        return "user/track_booking";
    }
	

	    @Autowired
	    private BookingRepository bookingRepo;

	   
	   
	    @Transactional
	    @GetMapping("/user/bookings/cancel/{bookingId}")
	    public String showCancelPage(@PathVariable int bookingId, Authentication authentication, Model model) {
	        System.out.println(">>> Cancel request received for booking ID: " + bookingId);

	        String loggedInEmail = authentication.getName(); 

	        Booking booking = bookingRepo.findById(bookingId).orElse(null);
	        if (booking == null || !booking.getUser().getEmail().equalsIgnoreCase(loggedInEmail) || booking.isCancelled()) {
	            return "redirect:/user/bookings";
	        }

	        model.addAttribute("booking", booking);
	        return "user/cancel-booking";
	    }

	    @Transactional
	    @PostMapping("/user/bookings/cancel/{bookingId}")
	    public String cancelBooking(@PathVariable int bookingId,
	                                @RequestParam String reason,
	                                Authentication authentication,
	                                RedirectAttributes redirectAttributes) {

	        System.out.println(">>> Cancel request received for booking ID: " + bookingId);

	        Booking booking = bookingRepo.findById(bookingId).orElse(null);
	        if (booking == null) {
	            System.out.println(">>> Booking is NULL");
	            return "redirect:/user/bookings";
	        }

	        String loggedInEmail = authentication.getName();
	        System.out.println(">>> Booking user email: " + booking.getUser().getEmail());
	        System.out.println(">>> Logged in user email: " + loggedInEmail);

	        if (!booking.getUser().getEmail().equalsIgnoreCase(loggedInEmail)) {
	            System.out.println(">>> Emails do not match!");
	            return "redirect:/user/bookings";
	        }

	        if (booking.isCancelled()) {
	            System.out.println(">>> Booking already cancelled");
	            return "redirect:/user/bookings";
	        }

	        
	        booking.setCancelled(true);
	        booking.setStatus("Cancelled");
	        booking.setCancellationReason(reason);
	        booking.setCancellationDate(LocalDateTime.now());

	        LocalDateTime bookingTime = booking.getBookingDate();
	        LocalDateTime now = LocalDateTime.now();

	        if (now.isBefore(bookingTime.plusHours(24))) {
	            booking.setRefundAmount(booking.getTotalAmount());
	            System.out.println(">>> Eligible for refund (cancelled within 24 hours of booking creation)");
	        } else {
	            booking.setRefundAmount(0);
	            System.out.println(">>> Not eligible for refund (cancelled after 24 hours)");
	        }

	        bookingRepo.save(booking);
	        System.out.println(">>> Booking cancelled and saved successfully!");

	        
	        emailService.sendStatusUpdateEmail(
	            booking.getUser().getEmail(),
	            booking.getUser().getName(),
	            booking.getId().intValue(),
	            booking.getService().getTitle(),
	            booking.getStatus(),
	            booking.getTotalAmount(),
	            booking.getBookingDate().toString()
	        );
	        System.out.println(">>> Cancellation email sent to user!");

	        redirectAttributes.addFlashAttribute("message",
	            "Booking cancelled successfully! Refund amount: ₹" + booking.getRefundAmount());

	        return "redirect:/user/bookings";
	    }


	}






