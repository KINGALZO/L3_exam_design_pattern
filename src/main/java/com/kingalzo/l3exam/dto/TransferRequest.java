package com.kingalzo.l3exam.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class TransferRequest {

    @NotBlank(message = "senderPhone is required")
    private String senderPhone;

    @NotBlank(message = "receiverPhone is required")
    private String receiverPhone;

    @Positive(message = "amount must be positive")
    private double amount;

    public TransferRequest() {
    }

    public String getSenderPhone() {
        return senderPhone;
    }

    public void setSenderPhone(String senderPhone) {
        this.senderPhone = senderPhone;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
