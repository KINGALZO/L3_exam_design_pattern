package com.kingalzo.l3exam.strategy;

import com.kingalzo.l3exam.domain.Wallet;
import com.kingalzo.l3exam.domain.Transaction;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class CreditCardDepositStrategy implements DepositStrategy {

    @Override
    public void execute(Wallet wallet, double amount) {
        // Simulate credit card deposit logic
        BigDecimal depositAmount = BigDecimal.valueOf(amount).setScale(2, BigDecimal.ROUND_HALF_UP);
        
        // Update wallet balance
        wallet.setBalance(wallet.getBalance().add(depositAmount));
        
        // Record transaction
        Transaction transaction = new Transaction(depositAmount, Transaction.TransactionType.CREDIT);
        wallet.addTransaction(transaction);
    }

    @Override
    public String getStrategyName() {
        return "CREDIT_CARD";
    }
}
