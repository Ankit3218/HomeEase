
package com.homeease.service;
import java.util.List;

import org.slf4j.Logger;



import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.homeease.config.EmailService;
import com.homeease.entity.Booking;
import com.homeease.entity.User;
import com.homeease.repository.BookingRepository;
import com.homeease.repository.UserRepository;

@Service
public class BookingService {

    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private EmailService emailService;
    
    @Autowired
    private UserRepository userRepository;

    public void updateBookingStatus(int bookingId, String newStatus) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        booking.setStatus(newStatus);
        bookingRepository.save(booking);

        String html = "<h3>Booking Status Updated</h3>" +
                "<p>Hi " + booking.getCustomerName() + ",</p>" +
                "<p>Your booking ID <b>" + booking.getId() + "</b> is now updated to: <b>" + newStatus + "</b>.</p>" +
                "<p>Thanks,<br>HomeEase Team</p>";

        logger.info("Sending booking status update email to: {}", booking.getUser().getEmail());
        emailService.sendHtmlEmail(booking.getUser().getEmail(), "Booking Status Updated", html);
    }

    public void confirmBooking(Booking booking) {
        try {
            // Save booking and get the updated entity with generated fields
            Booking savedBooking = bookingRepository.save(booking);

            //  email content using savedBooking data
            String html = "<h2>Booking Confirmed</h2>" +
                    "<p>Hi " + savedBooking.getCustomerName() + ",</p>" +
                    "<p>Your booking for <b>" + savedBooking.getService().getTitle() + "</b> is confirmed.</p>" +
                    "<p>Date: " + savedBooking.getBookingDate().toLocalDate() + "<br>" +
                    "Total Amount: ₹" + savedBooking.getTotalAmount() + "</p>" +
                    "<p>Thanks,<br>HomeEase Team</p>";

            // Log info for debugging
            logger.info("Sending booking confirmation email to: {}", savedBooking.getUser().getEmail());

            // Send the confirmation email
            emailService.sendHtmlEmail(savedBooking.getUser().getEmail(), "Booking Confirmation - HomeEase", html);

        } catch (Exception e) {
            // Log any error during saving or email sending
            logger.error("Error while confirming booking or sending email", e);
        }
    }
    
    public List<Booking> getBookingsByUsername(String username) {
        User user = userRepository.findByEmail(username); 
        if (user == null) {
            throw new RuntimeException("User not found with email: " + username);
        }
        return bookingRepository.findByUser(user);
    }


}