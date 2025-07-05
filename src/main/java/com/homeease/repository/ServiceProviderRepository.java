package com.homeease.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.homeease.entity.ServiceProvider;

public interface ServiceProviderRepository extends JpaRepository<ServiceProvider, Long> {

    // Find provider by email (used in login)
    ServiceProvider findByEmail(String email);

    // Check if email already exists (used in registration)
    boolean existsByEmail(String email);  // ✅ Add this line

    // Fetch providers by assigned service ID
    List<ServiceProvider> findByAssignedServices_Id(Long serviceId);
}
