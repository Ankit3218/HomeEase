package com.homeease.controller;

import com.homeease.entity.Booking;
import com.homeease.entity.Review;
import com.homeease.entity.Service;
import com.homeease.entity.User;
import com.homeease.repository.BookingRepository;
import com.homeease.repository.ServiceRepository;
import com.homeease.service.ReviewService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;


@Controller
public class ServiceController {

    @Autowired
    private ServiceRepository serviceRepository;
    
    @Autowired
    private ReviewService reviewService;
    
    @Autowired
    private ServiceRepository serviceRepo;
    
    @Autowired
    private BookingRepository bookingRepo;

/*
    @GetMapping("/services")
    public String showServices(Model model) {
        List<Service> services = serviceRepo.findAll();
        if (services == null) {
            services = new ArrayList<>();  // avoid null list
        }
        model.addAttribute("services", services);
    

        return "service/list";  // points to src/main/resources/templates/service/list.html
    }
    */
    @GetMapping("/service/details/{id}")
    public String serviceDetails(@PathVariable int id, HttpSession session, Model model) {
        Service service = serviceRepo.findById(id).orElseThrow();
        User user = (User) session.getAttribute("user");

        List<Review> reviews = reviewService.getReviewsByService(service);
        Review userReview = null;
        boolean hasBooked = false;

        if (user != null) {
            userReview = reviewService.getReviewByUserAndService(user, service).orElse(null);
            hasBooked = bookingRepo.existsByUserAndService(user, service);
        }

        model.addAttribute("service", service);
        model.addAttribute("booking", new Booking());
        model.addAttribute("reviews", reviews);
        model.addAttribute("userReview", userReview);
        model.addAttribute("hasBooked", hasBooked); 

        return "user/service-detail";
    }

}
