package com.homeease.controller;

import com.homeease.entity.*;
import com.homeease.repository.*;
import com.homeease.service.ReviewService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.security.Principal;
import java.util.List;
import java.util.Optional;


@Controller
@RequestMapping("/user/review")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;
    
    @Autowired
    private ReviewRepository reviewRepo;

    @Autowired
    private ServiceRepository serviceRepo;

    @Autowired
    private UserRepository userRepo;
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/save")
    public String saveReview(@RequestParam int rating,
                             @RequestParam String comment,
                             @RequestParam Integer serviceId,
                             @RequestParam(required = false) Integer reviewId,
                             HttpSession session) {

        User user = (User) session.getAttribute("user");
        Service service = serviceRepo.findById(serviceId).orElseThrow();

        Review review;
        if (reviewId != null) {
            review = reviewService.getReviewById(reviewId)
                    .orElse(new Review());
        } else {
            review = reviewService.getReviewByUserAndService(user, service)
                    .orElse(new Review());
        }

        review.setUser(user);
        review.setService(service);
        review.setRating(rating);
        review.setComment(comment);

        reviewService.saveReview(review);
        return "redirect:/user/profile";
    }

    @PostMapping("/delete/{id}")
    public String deleteReview(@PathVariable int id, HttpSession session) {
        User user = (User) session.getAttribute("user");
        Review review = reviewService.getReviewById(id)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        if (!review.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You can delete only your own reviews");
        }

        reviewService.deleteReview(id);
        return "redirect:/service/details/" + review.getService().getId();
    }
    
    @GetMapping("/service/details/{id}")
    public String showServiceDetails(@PathVariable("id") Integer id, Model model, HttpSession session) {
        Service service = serviceRepo.findById(id).orElseThrow(() -> new RuntimeException("Service not found"));

        User user = (User) session.getAttribute("user");
        List<Review> reviews = reviewService.getReviewsByService(service);

        Review userReview = null;
        if (user != null) {
            userReview = reviewService.getReviewByUserAndService(user, service).orElse(null);
        }

        model.addAttribute("service", service);
        model.addAttribute("reviews", reviews);
        model.addAttribute("userReview", userReview);
        model.addAttribute("booking", new Booking()); 

        return "user/service-detail"; 
    }
    
    /*
    
    @PostMapping("/user/review/save")
    public String saveReview(@ModelAttribute Review review, Principal principal) {
        if (principal == null) return "redirect:/login";

        User user = userRepository.findByEmail(principal.getName());
        Service service = serviceRepo.findById(review.getService().getId()).orElse(null);
        if (service == null) return "redirect:/";

        review.setUser(user);
        review.setService(service);

        // Check if this is an update or new review
        Optional<Review> existing = reviewRepo.findByUserAndService(user, service);
        if (existing.isPresent()) {
            review.setId(existing.get().getId()); // update existing review
        }

        reviewRepo.save(review);

        return "redirect:/user/profile";  // ✅ Redirect to Profile page
    }
    
    */

}


