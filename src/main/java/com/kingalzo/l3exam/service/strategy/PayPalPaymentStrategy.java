package com.kingalzo.l3exam.service.strategy;

import com.kingalzo.l3exam.service.PaymentStrategy;

public class PayPalPaymentStrategy implements PaymentStrategy {
    @Override
    public String pay(double amount) {
        return "Payment of " + amount + "€ processed via PayPal.";
    }
}
