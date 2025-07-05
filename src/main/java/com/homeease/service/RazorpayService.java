package com.homeease.service;


import com.razorpay.*;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class RazorpayService {

    private final String KEY = "rzp_test_c0hU261YbDuwY4";
    private final String SECRET = "MrzxStgRsNFjrRSPqLzr3S0l";

    public Order createOrder(int amount) throws RazorpayException {
        RazorpayClient client = new RazorpayClient(KEY, SECRET);

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amount * 100); // Amount in paise (e.g., 500 = ₹5.00)
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "order_rcptid_" + System.currentTimeMillis());

        return client.orders.create(orderRequest);
    }
}

