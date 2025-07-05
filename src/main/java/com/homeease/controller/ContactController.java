package com.homeease.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.homeease.config.EmailService;

@Controller
@RequestMapping("/user")
public class ContactController {

    @Autowired
    private EmailService emailService;

  
    @GetMapping("/contact")
    public String showContactForm() {
        
        return "user/contact";
    }

    
    @PostMapping("/contact")
    public String handleContactForm(
        @RequestParam String name,
        @RequestParam String email,
        @RequestParam String message,
        Model model
    ) {
        String subject = "New Contact Us Message from " + name;
        String content = "<p><b>Name:</b> " + name + "</p>" +
                         "<p><b>Email:</b> " + email + "</p>" +
                         "<p><b>Message:</b><br>" + message + "</p>";

        String adminEmail = "ankitk3218@gmail.com";
        emailService.sendHtmlEmail(adminEmail, subject, content);

        model.addAttribute("successMessage", "Thank you for contacting us! We will get back to you soon.");

        
        return "redirect:/user/profile";
    }
}
