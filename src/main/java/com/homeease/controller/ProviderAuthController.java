package com.homeease.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.homeease.entity.Booking;
import com.homeease.entity.Service;
import com.homeease.entity.ServiceProvider;
import com.homeease.repository.BookingRepository;
import com.homeease.repository.ServiceProviderRepository;
import com.homeease.repository.ServiceRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/provider")
public class ProviderAuthController {

    @Autowired private ServiceProviderRepository providerRepo;
    @Autowired private ServiceRepository serviceRepo;
    @Autowired private BookingRepository bookingRepo;
    @Autowired private PasswordEncoder passwordEncoder;

   
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("provider", new ServiceProvider());
        model.addAttribute("services", serviceRepo.findAll());
        return "provider/register";
    }

    
    @PostMapping("/register")
    public String registerProvider(@ModelAttribute ServiceProvider provider,
                                   @RequestParam("serviceIds") List<Integer> serviceIds,
                                   RedirectAttributes redirectAttributes) {
        
        if (providerRepo.existsByEmail(provider.getEmail())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Email is already registered.");
            return "redirect:/provider/register";
        }

        List<Service> selectedServices = serviceRepo.findAllById(serviceIds);
        provider.setAssignedServices(selectedServices);

       
        String rawPassword = provider.getPassword();
        String hashedPassword = passwordEncoder.encode(rawPassword);
        provider.setPassword(hashedPassword);

        providerRepo.save(provider);
        redirectAttributes.addFlashAttribute("successMessage", "Registration successful. You can now log in.");
        return "redirect:/provider/login";
    }

    
    @GetMapping("/login")
    public String showLogin() {
        return "provider/login";
    }

    @PostMapping("/login")
    public String doLogin(@RequestParam String email,
                          @RequestParam String password,
                          HttpSession session,
                          RedirectAttributes redirectAttributes) {

        ServiceProvider provider = providerRepo.findByEmail(email);
        if (provider != null && passwordEncoder.matches(password, provider.getPassword())) {
            session.setAttribute("provider", provider);
            return "redirect:/provider/dashboard";
        }

        redirectAttributes.addFlashAttribute("errorMessage", "Invalid email or password.");
        return "redirect:/provider/login";
    }

    
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        ServiceProvider provider = (ServiceProvider) session.getAttribute("provider");
        if (provider == null) return "redirect:/provider/login";

        List<Booking> bookings = bookingRepo.findByAssignedProvider_Id(provider.getId());
        model.addAttribute("bookings", bookings);
        return "provider/dashboard";
    }

    
    @PostMapping("/booking/{id}/update")
    public String updateProviderStatus(@PathVariable int id, @RequestParam String providerStatus) {
        Booking booking = bookingRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        booking.setProviderStatus(providerStatus);
        bookingRepo.save(booking);
        return "redirect:/provider/dashboard";
    }

    
    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/provider/login";
    }
}
