package com.kingalzo.l3exam.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

public class WalletCreationRequest {

    @NotBlank(message = "phoneNumber is required")
    private String phoneNumber;

    @Email(message = "email must be valid")
    private String email;

    @PositiveOrZero(message = "initialBalance must be positive or zero")
    private double initialBalance = 0.0;

    @NotBlank(message = "code is required")
    private String code;

    private String currency = "XOF";

    public WalletCreationRequest() {
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public double getInitialBalance() {
        return initialBalance;
    }

    public void setInitialBalance(double initialBalance) {
        this.initialBalance = initialBalance;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
