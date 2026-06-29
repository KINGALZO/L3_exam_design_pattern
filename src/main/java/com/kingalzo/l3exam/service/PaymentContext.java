package com.kingalzo.l3exam.service;

public class PaymentContext {
    private final PaymentStrategy strategy;

    public PaymentContext(PaymentStrategy strategy) {
        this.strategy = strategy;
    }

    public String executePayment(double amount) {
        return strategy.pay(amount);
    }
}
