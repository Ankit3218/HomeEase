package com.homeease.entity;

public class CartItem {
    private Service service;
    private int quantity;

    public CartItem(Service service) {
        this.service = service;
        this.quantity = 1;
    }

    public Service getService() {
        return service;
    }

    public int getQuantity() {
        return quantity;
    }

    public void incrementQuantity() {
        this.quantity++;
    }

    public double getTotalPrice() {
        return service.getPrice() * quantity;
    }
}
