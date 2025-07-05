package com.homeease.controller;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.homeease.config.EmailService;
import com.homeease.entity.User;
import com.homeease.repository.UserRepository;


@Controller
public class ForgotPasswordController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "auth/forgot_password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam String email, Model model) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            String token = UUID.randomUUID().toString();
            user.setResetToken(token);
            user.setTokenExpiration(LocalDateTime.now().plusMinutes(15));
            userRepository.save(user);

            String resetLink = "http://localhost:8082/reset-password?token=" + token;
            String subject = "Reset your HomeEase password";
            String content = "<h2>Hello " + user.getName() + ",</h2>"
                    + "<p>Click below to reset your password:</p>"
                    + "<a href=\"" + resetLink + "\">Reset Password</a>"
                    + "<p>This link will expire in 15 minutes.</p>";

            emailService.sendHtmlEmail(user.getEmail(), subject, content);
        }
        model.addAttribute("message", "If your email is registered, a reset link has been sent.");
        return "auth/forgot_password";
    }
}
