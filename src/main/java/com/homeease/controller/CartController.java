
/*

package com.homeease.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;

import com.homeease.entity.*;
import com.homeease.repository.*;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class CartController {

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    // 1. Add to Cart
    @PostMapping("/cart/add")
    public String addToCart(@RequestParam("serviceId") Integer serviceId, HttpSession session) {
        Optional<Service> optionalService = serviceRepository.findById(serviceId);
        if (optionalService.isEmpty()) return "redirect:/services";

        Service service = optionalService.get();
        Map<Integer, CartItem> cart = (Map<Integer, CartItem>) session.getAttribute("cart");
        if (cart == null) cart = new HashMap<>();

        if (cart.containsKey(serviceId)) {
            cart.get(serviceId).incrementQuantity();
        } else {
            cart.put(serviceId, new CartItem(service));
        }

        session.setAttribute("cart", cart);
        return "redirect:/";
    }

    // 2. View Cart
    @GetMapping("/cart")
    public String viewCart(HttpSession session, Model model) {
        Map<Integer, CartItem> cart = (Map<Integer, CartItem>) session.getAttribute("cart");
        if (cart == null) cart = new HashMap<>();

        double total = cart.values().stream().mapToDouble(CartItem::getTotalPrice).sum();
        model.addAttribute("cart", cart);
        model.addAttribute("total", total);
        return "cart";
    }

    // 3. Clear Cart
    @GetMapping("/clear-cart")
    public String clearCart(HttpSession session) {
        session.removeAttribute("cart");
        return "redirect:/cart";
    }

    // 4. Checkout Page
    @GetMapping("/checkout")
    public String checkout(HttpSession session, Model model) {
        Map<Integer, CartItem> cart = (Map<Integer, CartItem>) session.getAttribute("cart");
        if (cart == null || cart.isEmpty()) return "redirect:/cart";

        double total = cart.values().stream().mapToDouble(CartItem::getTotalPrice).sum();
        model.addAttribute("total", total);
        return "checkout";
    }

    // 5. Confirm Booking
    @PostMapping("/booking-confirmation")
    public String confirmBooking(@RequestParam String paymentMode, HttpSession session, Model model, Principal principal) {
        Map<Integer, CartItem> cart = (Map<Integer, CartItem>) session.getAttribute("cart");
        if (cart == null || cart.isEmpty()) return "redirect:/cart";

        User user = userRepository.findByEmail(principal.getName());

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setBookingDate(LocalDateTime.now());
        booking.setPaymentMode(paymentMode);
        booking.setTotalAmount(cart.values().stream().mapToDouble(CartItem::getTotalPrice).sum());

     /*   for (CartItem cartItem : cart.values()) {
            BookingItem item = new BookingItem();
            item.setServiceName(cartItem.getService().getTitle());
            item.setPrice(cartItem.getService().getPrice());
            item.setQuantity(cartItem.getQuantity());
            item.setBooking(booking);
            booking.getItems().add(item);
        }

        bookingRepository.save(booking);
        session.removeAttribute("cart");

        model.addAttribute("message", "Booking confirmed! Total Amount: ₹" + booking.getTotalAmount());
        return "booking-confirmation";
    }
}
*/
