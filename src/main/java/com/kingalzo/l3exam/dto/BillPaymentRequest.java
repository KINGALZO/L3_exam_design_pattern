package com.kingalzo.l3exam.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class BillPaymentRequest {

    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    @NotBlank(message = "Service name is required")
    private String serviceName;

    @Positive(message = "Amount must be positive")
    private double amount;

    public BillPaymentRequest() {}

    public BillPaymentRequest(String phoneNumber, String serviceName, double amount) {
        this.phoneNumber = phoneNumber;
        this.serviceName = serviceName;
        this.amount = amount;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
