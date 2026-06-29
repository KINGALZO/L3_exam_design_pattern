package com.kingalzo.l3exam.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class WithdrawalRequest {

    @NotBlank(message = "phoneNumber is required")
    private String phoneNumber;

    @Positive(message = "amount must be positive")
    private double amount;

    public WithdrawalRequest() {
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
