package com.kingalzo.l3exam.exception;

public class PaymentServiceNotFoundException extends RuntimeException {

    public PaymentServiceNotFoundException(String message) {
        super(message);
    }

    public PaymentServiceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
