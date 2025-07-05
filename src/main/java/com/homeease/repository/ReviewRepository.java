package com.homeease.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.homeease.entity.Review;
import com.homeease.entity.User;
import com.homeease.entity.Service;

public interface ReviewRepository extends JpaRepository<Review, Integer> {

    List<Review> findByService(Service service);

    Optional<Review> findByUserAndService(User user, Service service);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.service.id = :serviceId")
    Double getAverageRatingByService(@Param("serviceId") int serviceId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.service.id = :serviceId")
    Long countByServiceId(@Param("serviceId") int serviceId);

   
    List<Review> findByUser(User user);
}
