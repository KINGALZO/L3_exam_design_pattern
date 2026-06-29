package com.kingalzo.l3exam.service;

import com.kingalzo.l3exam.model.Order;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {
    private final List<Order> allOrders = new ArrayList<>();

    public Order createOrder(Long productId, int quantity) {
        Order order = new Order(productId, quantity, "CREATED");
        allOrders.add(order);
        return order;
    }

    public List<Order> getOrders() {
        return List.copyOf(allOrders);
    }
}
