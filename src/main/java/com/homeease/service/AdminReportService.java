package com.homeease.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.homeease.entity.Booking;
import com.homeease.repository.BookingRepository;

@Service
public class AdminReportService {

    @Autowired
    private BookingRepository bookingRepository;

    
    public Map<String, Long> getBookingCountByStatus() {
        return bookingRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(Booking::getStatus, Collectors.counting()));
    }

    public Map<YearMonth, Long> getMonthlyBookingsCount() {
        LocalDate sixMonthsAgo = LocalDate.now().minusMonths(6);
        return bookingRepository.findAll()
            .stream()
            .filter(b -> b.getBookingDate().toLocalDate().isAfter(sixMonthsAgo))
            .collect(Collectors.groupingBy(b -> YearMonth.from(b.getBookingDate()), Collectors.counting()));
    }

    public double getTotalRevenue() {
        return bookingRepository.findAll()
            .stream()
            .mapToDouble(Booking::getTotalAmount)
            .sum();
    }
}

