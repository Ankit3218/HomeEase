package com.homeease.controller;



import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller

public class AdminPolicyController {

    @GetMapping("/terms")
    public String showAdminTerms() {
        return "admin/terms"; 
    }

    @GetMapping("/privacy")
    public String showAdminPrivacy() {
        return "admin/privacy"; 
    }
}
