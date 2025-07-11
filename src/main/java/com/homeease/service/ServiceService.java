package com.homeease.service;

import com.homeease.entity.Service;
import java.util.List;

public interface ServiceService {
    List<Service> getAllServices();
    Service getServiceById(Long id);   
    void saveService(Service service);
    void deleteService(Long id);
}

