package com.homeease.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.homeease.entity.Booking;
import com.homeease.entity.Service;
import com.homeease.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Repository;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
	
	List<Booking> findByUser(User user);
	boolean existsByUserAndService(User user, Service service);
	

	List<Booking> findByUserAndCancelledTrue(User user);
	Optional<Booking> findById(Integer id); 

    Page<Booking> findAll(Pageable pageable);
    Page<Booking> findByUserEmailContainingIgnoreCase(String email, Pageable pageable);
    Page<Booking> findById(Long id, Pageable pageable);
    List<Booking> findByAssignedProvider_Id(Long providerId);
    
    




}
