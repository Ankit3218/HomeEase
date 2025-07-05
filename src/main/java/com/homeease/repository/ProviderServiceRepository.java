package com.homeease.repository;

import com.homeease.entity.ProviderService;
import com.homeease.entity.ServiceProvider;
import com.homeease.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProviderServiceRepository extends JpaRepository<ProviderService, Long> {

    List<ProviderService> findByService(Service service);

    List<ProviderService> findByProvider(ServiceProvider provider);

    List<ProviderService> findByServiceId(Long serviceId);

    List<ProviderService> findByProviderId(Long providerId);
}
