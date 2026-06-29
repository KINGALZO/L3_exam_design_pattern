package com.kingalzo.l3exam.strategy;

import com.kingalzo.l3exam.domain.Wallet;

public interface DepositStrategy {
    void execute(Wallet wallet, double amount);
    String getStrategyName();
}
