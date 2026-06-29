package com.kingalzo.l3exam.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.NotBlank;

public class DepositRequest {

    @Positive(message = "amount must be positive")
    private double amount;

    @NotBlank(message = "paymentMethod is required")
    private String paymentMethod; // CREDIT_CARD or WALLET_TARGET

    public DepositRequest() {
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
