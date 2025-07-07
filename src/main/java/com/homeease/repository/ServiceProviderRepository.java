package com.homeease.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.homeease.entity.ServiceProvider;

public interface ServiceProviderRepository extends JpaRepository<ServiceProvider, Long> {

    
    ServiceProvider findByEmail(String email);


    boolean existsByEmail(String email);  


    List<ServiceProvider> findByAssignedServices_Id(Long serviceId);
}
