package com.kingalzo.l3exam.strategy;

import com.kingalzo.l3exam.domain.Wallet;
import java.util.List;

public interface BillPaymentStrategy {

    void payBill(Wallet wallet, double amount);

    void payMultipleBills(Wallet wallet, List<String> billReferences);

    String getServiceName();
}
