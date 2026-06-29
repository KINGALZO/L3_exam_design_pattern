package com.kingalzo.l3exam.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class MultipleBillPaymentRequest {

    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    @NotBlank(message = "Service name is required")
    private String serviceName;

    @NotEmpty(message = "Bill references list cannot be empty")
    private List<String> billReferences;

    public MultipleBillPaymentRequest() {}

    public MultipleBillPaymentRequest(String phoneNumber, String serviceName, List<String> billReferences) {
        this.phoneNumber = phoneNumber;
        this.serviceName = serviceName;
        this.billReferences = billReferences;
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

    public List<String> getBillReferences() {
        return billReferences;
    }

    public void setBillReferences(List<String> billReferences) {
        this.billReferences = billReferences;
    }
}
