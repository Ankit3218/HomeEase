package com.homeease.config;

import com.homeease.entity.EmailLog;
import org.springframework.core.io.ByteArrayResource;

import com.homeease.repository.EmailLogRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.springframework.core.io.ClassPathResource;
@Async
@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private EmailLogRepository emailLogRepository;

    @Autowired
    private TemplateEngine templateEngine;
    public void sendStatusUpdateEmail(String toEmail, String name, int bookingId, String serviceTitle, String status, double totalAmount, String bookingDateTime) {
        try {
            Context context = new Context();
            context.setVariable("name", name);
            context.setVariable("bookingId", bookingId);
            context.setVariable("status", status);
            context.setVariable("serviceTitle", serviceTitle);
            context.setVariable("totalAmount", totalAmount);
            context.setVariable("bookingDateTime", bookingDateTime);

            String htmlBody = templateEngine.process("admin/status_update_email", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom("ankitk3218@gmail.com");
            helper.setTo(toEmail);
            helper.setSubject("Your Booking Status Updated");
            helper.setText(htmlBody, true);

           
            helper.addInline("logo", new ClassPathResource("static/images/logo.png"));

            mailSender.send(message);

            EmailLog log = new EmailLog();
            log.setRecipient(toEmail);
            log.setSubject("Your Booking Status Updated");
            log.setContent(htmlBody);
            log.setSentAt(LocalDateTime.now());
            emailLogRepository.save(log);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void sendHtmlEmail(String toEmail, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom("ankitk3218@gmail.com");
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); 

            mailSender.send(message);

         
            EmailLog log = new EmailLog();
            log.setRecipient(toEmail);
            log.setSubject(subject);
            log.setContent(htmlContent);
            log.setSentAt(LocalDateTime.now());
            emailLogRepository.save(log);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
    
    public void sendWelcomeEmail(String toEmail, String name) {
        try {
            Context context = new Context();
            context.setVariable("name", name);

            String htmlBody = templateEngine.process("auth/welcome_email", context);
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom("ankitk3218@gmail.com");
            helper.setTo(toEmail);
            helper.setSubject("Welcome to HomeEase!");
            helper.setText(htmlBody, true);
            

            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void sendBookingConfirmation(String toEmail, String name, int bookingId, String serviceTitle) {
        try {
            Context context = new Context();
            context.setVariable("name", name);
            context.setVariable("bookingId", bookingId);
            context.setVariable("service", serviceTitle);

            String htmlBody = templateEngine.process("auth/booking_confirmation", context);
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom("ankitk3218@gmail.com");
            helper.setTo(toEmail);
            helper.setSubject("Booking Confirmation - HomeEase");
            helper.setText(htmlBody, true);

            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void sendBookingInvoice(String toEmail, String subject, String body, byte[] pdfBytes, String pdfFileName) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(toEmail);
        helper.setSubject(subject);
        helper.setText(body, true); 
        helper.addAttachment(pdfFileName, new ByteArrayResource(pdfBytes));

        mailSender.send(message);
    }

}
