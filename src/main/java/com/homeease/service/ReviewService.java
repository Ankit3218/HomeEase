package com.homeease.service;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;

import com.homeease.entity.Review;
import com.homeease.entity.Service;
import com.homeease.entity.User;
import com.homeease.repository.ReviewRepository;
import com.homeease.repository.ServiceRepository;
import com.homeease.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepo;
    
   
    public List<Review> getReviewsByService(Service service) {
        return reviewRepo.findByService(service);
    }

    public Optional<Review> getReviewByUserAndService(User user, Service service) {
        return reviewRepo.findByUserAndService(user, service);
    }

    public Optional<Review> getReviewById(int id) {
        return reviewRepo.findById(id);
    }

    public void saveReview(Review review) {
        review.setCreatedAt(LocalDateTime.now());
        reviewRepo.save(review);
    }

    public void deleteReview(int id) {
        reviewRepo.deleteById(id);
    }
    
   

}
