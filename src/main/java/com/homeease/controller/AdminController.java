package com.homeease.controller;

import com.homeease.config.EmailService;
import com.homeease.entity.Booking;
import com.homeease.entity.Service;
import com.homeease.entity.ServiceProvider;
import com.homeease.entity.User;
import com.homeease.entity.UserFlag;
import com.homeease.repository.BookingRepository;
import com.homeease.repository.ServiceProviderRepository;
import com.homeease.repository.ServiceRepository;
import com.homeease.repository.UserFlagRepository;
import com.homeease.repository.UserRepository;
import com.homeease.service.AdminReportService;
import com.homeease.service.BookingService;
import com.homeease.service.RazorpayService;
import com.razorpay.Order;
import com.razorpay.RazorpayException;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ServiceRepository serviceRepository;
    @Autowired
    private ServiceRepository serviceRepo;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private UserFlagRepository userFlagRepository;
    
    @Autowired
    private AdminReportService reportService;
    
    @Autowired
    private BookingService bookingService;
    
    
    @Autowired
    private EmailService emailService;



    
    
  
    @GetMapping("")
    public String adminHome(Model model) {
        long servicesCount = serviceRepository.count();
        long usersCount = userRepository.count();
        long bookingsCount = bookingRepository.count();

        model.addAttribute("servicesCount", servicesCount);
        model.addAttribute("usersCount", usersCount);
        model.addAttribute("bookingsCount", bookingsCount);
        model.addAttribute("bookingStatusCount", reportService.getBookingCountByStatus());
        model.addAttribute("monthlyBookingCount", reportService.getMonthlyBookingsCount());
        model.addAttribute("totalRevenue", reportService.getTotalRevenue());

        return "admin/dashboard";
    }


    
    @GetMapping("/services")
    public String listServices(Model model) {
        model.addAttribute("services", serviceRepository.findAll());
        return "admin/manage_services"; 
    }

 
    @GetMapping("/services/add")
    public String showAddForm(Model model) {
        model.addAttribute("service", new Service());
        return "admin/add-service"; 
    }

  
    @PostMapping("/services/add")
    public String addService(@ModelAttribute Service service) {
        serviceRepository.save(service);
        return "redirect:/admin/services";
    }

  
    @GetMapping("/services/edit/{id}")
    public String showEditForm(@PathVariable int id, Model model) {
        Service service = serviceRepository.findById(id).orElse(null);
        model.addAttribute("service", service);
        return "admin/edit-service"; 
    }

    
    @PostMapping("/services/update")
    public String updateService(@ModelAttribute Service service) {
        serviceRepository.save(service);
        return "redirect:/admin/services";
    }

  
    @GetMapping("/services/delete/{id}")
    public String deleteService(@PathVariable int id) {
        serviceRepository.deleteById(id);
        return "redirect:/admin/services";
    }
    
    

    
    @GetMapping("/bookings")
    public String viewAllBookings(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Long orderId,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        Pageable pageable = PageRequest.of(page, 5, Sort.by("bookingDate").descending());
        Page<Booking> bookings;

        if (orderId != null) {
            bookings = bookingRepository.findById(orderId, pageable);
        } else if (email != null && !email.isEmpty()) {
            bookings = bookingRepository.findByUserEmailContainingIgnoreCase(email, pageable);
        } else {
            bookings = bookingRepository.findAll(pageable);
        }

        model.addAttribute("bookings", bookings.getContent());
        model.addAttribute("totalPages", bookings.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("email", email);
        model.addAttribute("orderId", orderId);

        return "admin/bookings";
    }

    
    @Transactional
    @PostMapping("/bookings/updateStatus")
    public String updateBookingStatus(@RequestParam("id") int id, @RequestParam("status") String status) {
        Booking booking = bookingRepository.findById(id).orElse(null);
        if (booking != null) {
            booking.setStatus(status);

           
            if (status.equalsIgnoreCase("Cancelled")) {
                booking.setCancelled(true);
                booking.setCancellationDate(LocalDateTime.now());
                booking.setCancellationReason("Cancelled by Admin");

                
                LocalDateTime bookingTime = booking.getBookingDate();
    	        LocalDateTime now = LocalDateTime.now();
    	        if (now.isBefore(bookingTime.plusHours(24))) {
    	            booking.setRefundAmount(booking.getTotalAmount());
    	            System.out.println(">>> Eligible for refund (cancelled within 24 hours of booking creation)");
    	        } else {
    	            booking.setRefundAmount(0);
    	            System.out.println(">>> Not eligible for refund (cancelled after 24 hours)");
    	        }
            }

            bookingRepository.save(booking);

            emailService.sendStatusUpdateEmail(
                booking.getUser().getEmail(),
                booking.getUser().getName(),
                booking.getId().intValue(),
                booking.getService().getTitle(),
                status,
                booking.getTotalAmount(),
                booking.getBookingDate().toString()
            );
        }
        return "redirect:/admin/bookings";
    }



    
 
    @GetMapping("/users")
    public String viewAllUsers(
        Model model,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int size,
        @RequestParam(required = false) String keyword
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage;

        if (keyword != null && !keyword.isEmpty()) {
            userPage = userRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(keyword, keyword, pageable);
        } else {
            userPage = userRepository.findAll(pageable);
        }

        model.addAttribute("usersPage", userPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("keyword", keyword);

        return "admin/users";
    }
    
   
    @GetMapping("/userBookings/{id}")
    public String viewUserBookings(@PathVariable int id, Model model) {
    	User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        List<Booking> bookings = bookingRepository.findByUser(user);
        model.addAttribute("bookings", bookings);
        model.addAttribute("user", user);
        return "admin/user_bookings";
    }
    @GetMapping("/users/flag/{id}")
    public String flagUser(@PathVariable int id, @RequestParam("comment") String comment, Model model) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            UserFlag flag = new UserFlag(user, comment);
            userFlagRepository.save(flag);
        
            model.addAttribute("message", "User flagged successfully!");
        }
        return "redirect:/admin/users";
    }

    
    @PostMapping("/admin/users/delete/{id}")
    public String deleteUser(@PathVariable("id") int userId) {
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        
        List<Booking> bookings = bookingRepository.findByUser(user);
        bookingRepository.deleteAll(bookings);

        
        userRepository.delete(user);

        return "redirect:/admin/users?deleted";  
    }
    @GetMapping("/flagged-users")
    public String viewFlaggedUsers(Model model) {
        List<UserFlag> flaggedUsers = userFlagRepository.findAll();
        model.addAttribute("flaggedUsers", flaggedUsers);
        return "admin/flagged_users"; 
    }
    @GetMapping("/bookings/export")
    public void exportBookingsCsv(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=bookings.csv");

        List<Booking> bookings = bookingRepository.findAll();

        try (PrintWriter writer = response.getWriter()) {
            writer.println("ID,User,Service,Date,Status,Total Amount");
            for (Booking b : bookings) {
                writer.printf("%d,%s,%s,%s,%s,%.2f%n",
                    b.getId(),
                    b.getUser().getName(),
                    b.getService().getTitle(),
                    b.getBookingDate(),
                    b.getStatus(),
                    b.getTotalAmount());
            }
        }
    }

/*
   
    @PostMapping("/admin/bookings/update-status/{id}")
    public String updateStatus(@PathVariable("id") Integer id, @RequestParam String newStatus) {
        Booking booking = bookingRepository.findById(id).orElse(null);
        if (booking != null) {
            booking.setStatus(newStatus);
            bookingRepository.save(booking);

            // ✅ Send status update email
            String userEmail = booking.getUser().getEmail();
            String subject = "Booking Status Update - HomeEase";
            String content = "<h2>Hi " + booking.getUser().getName() + ",</h2>" +
                    "<p>Your service booking (ID: " + booking.getId() + ") has been updated.</p>" +
                    "<p><strong>Status:</strong> <span style='color:green'>" + newStatus + "</span></p>" +
                    "<p>We appreciate your patience.</p>";

            emailService.sendHtmlEmail(userEmail, subject, content);
        }

        return "redirect:/admin/bookings";
    }
/*
    @GetMapping("/test-email")
    @ResponseBody
    public String testEmail() {
        emailService.sendHtmlEmail("ark17072002@gmail.com", "Test Email", "<h1>This is a test from HomeEase</h1>");
        return "Test email sent.";
    }

*/
    
    
    
    @Autowired private ServiceProviderRepository providerRepo;
    @Autowired private BookingRepository bookingRepo;


    @GetMapping("/providers")
    public String listProviders(Model model) {
        model.addAttribute("providers", providerRepo.findAll());
        return "admin/provider-list";
    }

    @GetMapping("/providers/{id}/bookings")
    public String listProviderBookings(@PathVariable Long id, Model model) {
        model.addAttribute("bookings", bookingRepo.findByAssignedProvider_Id(id));
        return "admin/provider-bookings";
    }

    @GetMapping("/providers/{id}/edit")
    public String showEditProviderForm(@PathVariable Long id, Model model) {
        model.addAttribute("provider", providerRepo.findById(id).orElseThrow());
        model.addAttribute("allServices", serviceRepo.findAll());
        return "admin/edit-provider";
    }

    @PostMapping("/providers/{id}/update")
    public String updateProviderServices(
            @PathVariable Long id,
            @RequestParam(name = "serviceIds", required = false) List<Integer> serviceIds) {

        ServiceProvider p = providerRepo.findById(id).orElseThrow();
        p.setAssignedServices(
            serviceIds != null 
                ? serviceRepo.findAllById(serviceIds) 
                : new ArrayList<>()
        );
        providerRepo.save(p);
        return "redirect:/admin/providers";
    }
    @PostMapping("/bookings/{id}/status")
    public String updateAdminStatus(
            @PathVariable Integer id,
            @RequestParam("adminStatus") String adminStatus) {

        Booking booking = bookingRepo.findById(id).orElseThrow();
        booking.setAdminStatus(adminStatus);
        bookingRepo.save(booking);

        
        return "redirect:/admin/providers/" + booking.getAssignedProvider().getId() + "/bookings";
    }
    
    
    @Autowired
    private RazorpayService razorpayService;

    @PostMapping("/create-payment")
    public String createPayment(@RequestParam("amount") int amount, Model model) throws RazorpayException {
        Order order = razorpayService.createOrder(amount);

        model.addAttribute("orderId", order.get("id"));
        model.addAttribute("amount", amount);
        model.addAttribute("key","rzp_test_HIfeSJ9VMApjsQ");
        return "payment/checkout";
    }
    
  /*  @PostMapping("/book/confirm")
    public String confirmBooking(@ModelAttribute Booking booking,
                                 @RequestParam int serviceId,
                                 @RequestParam(required = false) Double finalPrice,
                                 @RequestParam(required = false) String couponCode,
                                 @RequestParam(required = false) String paymentId,
                                 Principal principal) {

        Optional<Service> optional = serviceRepo.findById(serviceId);
        if (optional.isEmpty()) return "redirect:/services";

        Service service = optional.get();
        booking.setService(service);
        booking.setBookingDate(LocalDateTime.now());
        booking.setStatus("Pending");

        if (finalPrice != null) {
            booking.setTotalAmount(finalPrice);
        } else {
            booking.setTotalAmount(service.getPrice());
        }

        if (principal != null) {
            User user = userRepository.findByEmail(principal.getName());
            booking.setUser(user);
        }

        if (paymentId != null && !paymentId.isBlank()) {
            booking.setPaymentId(paymentId); // ✅ Save Razorpay payment ID
        }

        List<ServiceProvider> availableProviders = providerRepo.findByAssignedServices_Id((long) serviceId);
        if (!availableProviders.isEmpty()) {
            booking.setAssignedProvider(availableProviders.get(0));
        }

        bookingRepo.save(booking);
        return "redirect:/booking-confirmation?amount=" + booking.getTotalAmount();
    }

*/




}
