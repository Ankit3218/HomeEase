package com.homeease.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.homeease.entity.Admin;
import com.homeease.repository.AdminRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class AdminLoginController {


@Autowired
private AdminRepository adminRepo;

@GetMapping("/admin/login")
public String showLoginForm() {
    return "admin/login";
}

@PostMapping("/admin/login")
public String processLogin(@RequestParam String username,
                           @RequestParam String password,
                           HttpSession session,
                           Model model) {
    Admin admin = adminRepo.findByUsernameAndPassword(username, password);
    if (admin != null) {
        session.setAttribute("admin", admin);
        return "redirect:/admin/services";
    } else {
        model.addAttribute("error", "Invalid Username or Password");
        return "admin/login";
    }
}

@GetMapping("/admin/logout")
public String logout(HttpSession session) {
    session.invalidate();
    return "redirect:/admin/login";
}
}