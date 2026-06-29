package com.kingalzo.l3exam.strategy;

import com.kingalzo.l3exam.domain.Wallet;
import com.kingalzo.l3exam.domain.Transaction;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;

@Component
public class IsmPaymentStrategy implements BillPaymentStrategy {

    private static final Logger logger = LoggerFactory.getLogger(IsmPaymentStrategy.class);

    @Override
    public void payBill(Wallet wallet, double amount) {
        logger.info("Processing ISM payment for wallet {} - Amount: {}", wallet.getPhone(), amount);
        
        BigDecimal billAmount = BigDecimal.valueOf(amount).setScale(2, BigDecimal.ROUND_HALF_UP);
        wallet.setBalance(wallet.getBalance().subtract(billAmount));
        
        Transaction transaction = new Transaction(billAmount, Transaction.TransactionType.PAYMENT);
        transaction.setWallet(wallet);
        wallet.addTransaction(transaction);
        
        logger.info("ISM payment completed - New balance: {}", wallet.getBalance());
    }

    @Override
    public void payMultipleBills(Wallet wallet, List<String> billReferences) {
        logger.info("Processing ISM multiple bills payment for wallet {} - Bills count: {}", 
                   wallet.getPhone(), billReferences.size());
        
        double totalAmount = 0;
        for (String billRef : billReferences) {
            double simulatedAmount = 5000.0; // Montant simulé par facture
            totalAmount += simulatedAmount;
            logger.debug("ISM Bill {} - Simulated amount: {}", billRef, simulatedAmount);
        }
        
        BigDecimal totalBillAmount = BigDecimal.valueOf(totalAmount).setScale(2, BigDecimal.ROUND_HALF_UP);
        wallet.setBalance(wallet.getBalance().subtract(totalBillAmount));
        
        Transaction transaction = new Transaction(totalBillAmount, Transaction.TransactionType.PAYMENT);
        transaction.setWallet(wallet);
        wallet.addTransaction(transaction);
        
        logger.info("ISM multiple bills payment completed - Total: {}, New balance: {}", 
                   totalBillAmount, wallet.getBalance());
    }

    @Override
    public String getServiceName() {
        return "ISM";
    }
}
