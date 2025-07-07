package com.homeease.service;


import com.razorpay.*;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class RazorpayService {

    private final String KEY = "rzp_test_HIfeSJ9VMApjsQ";
    private final String SECRET = "9R39lNzvqck98X32JuGzbjXD";

    public Order createOrder(int amount) throws RazorpayException {
        RazorpayClient client = new RazorpayClient(KEY, SECRET);

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amount * 100); 
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "order_rcptid_" + System.currentTimeMillis());

        return client.orders.create(orderRequest);
    }
}

