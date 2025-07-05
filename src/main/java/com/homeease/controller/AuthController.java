

package com.homeease.controller;

import java.security.Principal;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.homeease.config.EmailService;
import com.homeease.dto.RegisterDto;
import com.homeease.entity.User;
import com.homeease.repository.BookingRepository;
import com.homeease.repository.ServiceRepository;
import com.homeease.repository.UserFlagRepository;
import com.homeease.repository.UserRepository;
import com.homeease.service.AdminReportService;
import com.homeease.service.AuthService;

import jakarta.validation.Valid;






@Controller
public class AuthController {
	
	
	@Autowired
	private AuthService authService;
	
	@Autowired
	private EmailService emailService;

    @Autowired
    private ServiceRepository serviceRepository;

  
    
    @Autowired
    private BookingRepository bookingRepository;
    
   
    
    @Autowired
    private AdminReportService reportService;
    
 
    
    
    

	
	
	@GetMapping("/register")
	public String showRegisterForm(Model model) {
		
		
		model.addAttribute("user",new RegisterDto ());
		
		return  "auth/register";
		
	}
	
	

	@PostMapping("/register")
	public String registerUser(@Valid @ModelAttribute("user") RegisterDto dto, BindingResult result) {

	    if (result.hasErrors()) {
	        return "auth/register";
	    }

	    User registeredUser = authService.registerUser(dto); 

	    if (registeredUser != null) {
	        emailService.sendWelcomeEmail(registeredUser.getEmail(), registeredUser.getName());
	    }

	    return "redirect:/login";
	}

	@GetMapping("/login")
	public String showLoginForm() {
		return "auth/login";
	}
	
	 @Autowired
	 private UserRepository userRepository;

	 @GetMapping("/profile")
	 public String showProfilePage(Model model, Principal principal) {
	     String email = principal.getName(); 

	     
	     User user = userRepository.findByEmail(email);


	     model.addAttribute("user", user); 
	  
	     List<String> services = Arrays.asList("Plumbing", "Electrician", "Cleaning", "Painting", "Carpentry","PestControll");

	        model.addAttribute("services", services);
	     return "user/profile"; 
	 }

	 @Controller
	 @RequestMapping("/admin")
	 public class AdminController {

	     @GetMapping("/dashboard")
	     public String dashboard(Model model) {
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
	 }

	


}
