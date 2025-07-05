package com.homeease.controller;

import com.homeease.entity.*;
import com.homeease.repository.ProviderServiceRepository;
import com.homeease.repository.ServiceProviderRepository;
import com.homeease.repository.*;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Controller
public class UserController {

    @Autowired
    private ServiceRepository serviceRepo;

    @Autowired
    private BookingRepository bookingRepo;
 

        @Autowired
        private UserRepository userRepository;
        

        @Autowired
        private ReviewRepository reviewRepo;
        
        @Autowired
        private CouponRepository couponRepo; 
        
        @Autowired
        private ServiceProviderRepository providerRepo;

        @Autowired
        private ProviderServiceRepository providerServiceRepo;

    

        

    // Homepage - Show all services
    @GetMapping("/")
    public String viewHomePage(Model model) {
        model.addAttribute("services", serviceRepo.findAll());
        return "auth/welcome"; 
    }
    @GetMapping("/services")
    public String showServices(Model model) {
        List<Service> services = serviceRepo.findAll();

        Map<Integer, Double> avgRatings = new HashMap<>();
        Map<Integer, Long> reviewCounts = new HashMap<>();

        for (Service service : services) {
            Double avg = reviewRepo.getAverageRatingByService(service.getId());
            Long count = reviewRepo.countByServiceId(service.getId());

            avgRatings.put(service.getId(), avg != null ? avg : 0.0);
            reviewCounts.put(service.getId(), count);
        }

        model.addAttribute("services", services);
        model.addAttribute("avgRatings", avgRatings);
        model.addAttribute("reviewCounts", reviewCounts);
        return "service/list";  
    }

    
    /*
    // View service details
    @GetMapping("/services/{id}")
    public String viewServiceDetail(@PathVariable int id, Model model) {
        Service service = serviceRepo.findById(id).orElse(null);
        if (service == null) {
            return "redirect:/";
        }
        model.addAttribute("service", service);
        model.addAttribute("booking", new Booking());
        return "user/service-detail";
    }

    */
    
    
    
    @PostMapping("/book")
    public String showBookingFormWithCoupon(@RequestParam int serviceId,
                                            @RequestParam(required = false) String couponCode,
                                            Model model) {

        Optional<Service> optional = serviceRepo.findById(serviceId);
        if (optional.isEmpty()) return "redirect:/services";

        Service service = optional.get();
        double originalPrice = service.getPrice();
        double finalPrice = originalPrice;

        String couponSuccess = null;
        String couponError = null;

        if (couponCode != null && !couponCode.trim().isEmpty()) {
            Optional<Coupon> optionalCoupon = couponRepo.findByCodeAndActiveTrue(couponCode.trim());
            if (optionalCoupon.isPresent()) {
                Coupon coupon = optionalCoupon.get();
                if (coupon.getExpiryDate() == null || coupon.getExpiryDate().isAfter(LocalDate.now())) {
                    double discountAmount = originalPrice * coupon.getDiscount() / 100.0;
                    finalPrice = originalPrice - discountAmount;
                    couponSuccess = "Coupon applied! You saved ₹" + String.format("%.2f", discountAmount);
                } else {
                    couponError = "Coupon has expired.";
                }
            } else {
                couponError = "Invalid coupon code.";
            }
        }

        model.addAttribute("service", service);
        model.addAttribute("booking", new Booking());
        model.addAttribute("finalPrice", finalPrice);
        model.addAttribute("couponCode", couponCode);
        model.addAttribute("couponSuccess", couponSuccess);
        model.addAttribute("couponError", couponError);

        return "user/service-detail";
    }




  

    @PostMapping("/book/confirm")
    public String confirmBooking(@ModelAttribute Booking booking,
                                 @RequestParam int serviceId,
                                 @RequestParam(required = false) Double finalPrice,
                                 @RequestParam(required = false) String couponCode,
                                 @RequestParam(required = false) String paymentId,  // ✅ Razorpay Payment ID
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
            booking.setPaymentId(paymentId);
            booking.setPaymentMode("Razorpay"); 
        }

        
        List<ServiceProvider> availableProviders = providerRepo.findByAssignedServices_Id((long) serviceId);
        if (!availableProviders.isEmpty()) {
            booking.setAssignedProvider(availableProviders.get(0));
        }

        bookingRepo.save(booking);
        return "redirect:/booking-confirmation?amount=" + booking.getTotalAmount();
    }



   
   
    

    
    @GetMapping("/booking-confirmation")
    public String confirmation(@RequestParam("amount") double amount, Model model) {
        model.addAttribute("amount", amount);
        model.addAttribute("message", "Booking confirmed!");
       

        return "user/booking-confirmation";
    
    }
    
    
    
    
    @GetMapping("/user/bookings")
    public String viewMyBookings(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        User user = userRepository.findByEmail(principal.getName());
        List<Booking> allBookings = bookingRepo.findByUser(user);

        
        List<Booking> activeBookings = new ArrayList<>();
        List<Booking> cancelledBookings = new ArrayList<>();

      
        Map<Integer, Review> userReviews = new HashMap<>();

        for (Booking booking : allBookings) {
            if (booking.isCancelled()) {
                cancelledBookings.add(booking);
            } else {
                activeBookings.add(booking);
            }

            reviewRepo.findByUserAndService(user, booking.getService())
                      .ifPresent(review -> userReviews.put(booking.getService().getId(), review));
        }

       
        model.addAttribute("bookings", activeBookings);          
        model.addAttribute("cancelledBookings", cancelledBookings); 
        model.addAttribute("userReviews", userReviews);

        return "user/user-bookings";
    }

    
    
    
    @GetMapping("/user/profile")
    public String userProfile(Model model, Principal principal) {
        if (principal == null) return "redirect:/login";

        User user = userRepository.findByEmail(principal.getName());
        model.addAttribute("user", user);

        return "user/profile"; 
    }

    
    @GetMapping("/services/{id}")
    public String showServiceDetail(@PathVariable("id") int id, Model model, Principal principal) {
        Optional<Service> serviceOpt = serviceRepo.findById(id);
        if (serviceOpt.isEmpty()) {
            return "redirect:/services"; 
        }

        model.addAttribute("service", serviceOpt.get());

        if (principal != null) {
            User user = userRepository.findByEmail(principal.getName());
            List<Booking> userBookings = bookingRepo.findByUser(user);
            model.addAttribute("userBookings", userBookings); 
        }

        return "user/home";
    }

}  