package com.kingalzo.l3exam.service;

import com.kingalzo.l3exam.domain.Wallet;
import com.kingalzo.l3exam.domain.Transaction;
import com.kingalzo.l3exam.dto.WalletCreationRequest;
import com.kingalzo.l3exam.exception.WalletAlreadyExistsException;
import com.kingalzo.l3exam.exception.PaymentServiceNotFoundException;
import com.kingalzo.l3exam.repository.WalletRepository;
import com.kingalzo.l3exam.strategy.DepositStrategy;
import com.kingalzo.l3exam.strategy.BillPaymentStrategy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class WalletService {

    private final WalletRepository walletRepository;
    private final Map<String, DepositStrategy> strategyMap;
    private final Map<String, BillPaymentStrategy> billPaymentStrategyMap;

    public WalletService(WalletRepository walletRepository, List<DepositStrategy> strategies, List<BillPaymentStrategy> billPaymentStrategies) {
        this.walletRepository = walletRepository;
        this.strategyMap = strategies.stream()
                .collect(Collectors.toMap(DepositStrategy::getStrategyName, strategy -> strategy));
        this.billPaymentStrategyMap = billPaymentStrategies.stream()
                .collect(Collectors.toMap(BillPaymentStrategy::getServiceName, strategy -> strategy));
    }

    @Transactional
    public Wallet createWallet(WalletCreationRequest request) {
        String phone = request.getPhoneNumber();
        String code = request.getCode();

        if (phone != null && walletRepository.existsByPhone(phone)) {
            throw new WalletAlreadyExistsException("Wallet with phone " + phone + " already exists");
        }
        if (code != null && walletRepository.existsByCode(code)) {
            throw new WalletAlreadyExistsException("Wallet with code " + code + " already exists");
        }

        BigDecimal balance = BigDecimal.valueOf(request.getInitialBalance()).setScale(2, BigDecimal.ROUND_HALF_UP);
        Wallet wallet = new Wallet(phone, code, request.getEmail(), request.getCurrency(), balance);

        return walletRepository.save(wallet);
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public Page<Wallet> listWallets(Pageable pageable) {
        return walletRepository.findAll(pageable);
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public Wallet getWalletByPhone(String phone) {
        return walletRepository.findByPhone(phone)
                .orElseThrow(() -> new com.kingalzo.l3exam.exception.WalletNotFoundException("Wallet with phone " + phone + " not found"));
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public BigDecimal getBalanceByPhone(String phone) {
        Wallet w = walletRepository.findByPhone(phone)
                .orElseThrow(() -> new com.kingalzo.l3exam.exception.WalletNotFoundException("Wallet with phone " + phone + " not found"));
        return w.getBalance();
    }

    @Transactional
    public Wallet executeDeposit(Long walletId, String paymentMethod, double amount) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new com.kingalzo.l3exam.exception.WalletNotFoundException("Wallet with id " + walletId + " not found"));
        
        DepositStrategy strategy = strategyMap.get(paymentMethod);
        if (strategy == null) {
            throw new IllegalArgumentException("Invalid payment method: " + paymentMethod);
        }
        
        strategy.execute(wallet, amount);
        return walletRepository.save(wallet);
    }

    @Transactional
    public Wallet withdraw(String phoneNumber, double amount) {
        Wallet wallet = walletRepository.findByPhone(phoneNumber)
                .orElseThrow(() -> new com.kingalzo.l3exam.exception.WalletNotFoundException("Wallet with phone " + phoneNumber + " not found"));

        // Calculate withdrawal fee: 1% of amount, capped at 5000 CFA
        BigDecimal amountBD = BigDecimal.valueOf(amount).setScale(2, BigDecimal.ROUND_HALF_UP);
        BigDecimal feeAmount = amountBD.multiply(BigDecimal.valueOf(0.01)).setScale(2, BigDecimal.ROUND_HALF_UP);
        BigDecimal maxFee = BigDecimal.valueOf(5000).setScale(2, BigDecimal.ROUND_HALF_UP);
        if (feeAmount.compareTo(maxFee) > 0) {
            feeAmount = maxFee;
        }

        BigDecimal totalDebit = amountBD.add(feeAmount);

        // Verify sufficient balance
        if (wallet.getBalance().compareTo(totalDebit) < 0) {
            throw new com.kingalzo.l3exam.exception.SoldeInsuffisantException(
                    "Insufficient balance. Available: " + wallet.getBalance() + " CFA, Required: " + totalDebit + " CFA");
        }

        // Debit the wallet
        wallet.setBalance(wallet.getBalance().subtract(totalDebit));

        // Record withdrawal transaction
        Transaction transaction = new Transaction(amountBD, Transaction.TransactionType.DEBIT);
        wallet.addTransaction(transaction);

        return walletRepository.save(wallet);
    }

    @Transactional(rollbackFor = Exception.class)
    public void transfer(String senderPhone, String receiverPhone, double amount) {
        Wallet sender = walletRepository.findByPhone(senderPhone)
                .orElseThrow(() -> new com.kingalzo.l3exam.exception.WalletNotFoundException("Sender wallet with phone " + senderPhone + " not found"));

        Wallet receiver = walletRepository.findByPhone(receiverPhone)
                .orElseThrow(() -> new com.kingalzo.l3exam.exception.WalletNotFoundException("Receiver wallet with phone " + receiverPhone + " not found"));

        BigDecimal amountBD = BigDecimal.valueOf(amount).setScale(2, BigDecimal.ROUND_HALF_UP);

        // Verify sender has sufficient balance
        if (sender.getBalance().compareTo(amountBD) < 0) {
            throw new com.kingalzo.l3exam.exception.SoldeInsuffisantException(
                    "Insufficient balance. Available: " + sender.getBalance() + " CFA, Required: " + amountBD + " CFA");
        }

        // Debit sender
        sender.setBalance(sender.getBalance().subtract(amountBD));
        Transaction senderTransaction = new Transaction(amountBD, Transaction.TransactionType.DEBIT);
        sender.addTransaction(senderTransaction);

        // Credit receiver
        receiver.setBalance(receiver.getBalance().add(amountBD));
        Transaction receiverTransaction = new Transaction(amountBD, Transaction.TransactionType.CREDIT);
        receiver.addTransaction(receiverTransaction);

        // Save both wallets (atomically due to @Transactional)
        walletRepository.save(sender);
        walletRepository.save(receiver);
    }

    @Transactional
    public Wallet paySingleBill(String phoneNumber, String serviceName, double amount) {
        Wallet wallet = walletRepository.findByPhone(phoneNumber)
                .orElseThrow(() -> new com.kingalzo.l3exam.exception.WalletNotFoundException("Wallet with phone " + phoneNumber + " not found"));

        BillPaymentStrategy strategy = billPaymentStrategyMap.get(serviceName.toUpperCase());
        if (strategy == null) {
            throw new PaymentServiceNotFoundException("Payment service '" + serviceName + "' not found. Available services: ISM, WOYAFAL");
        }

        // Verify sufficient balance before payment
        BigDecimal amountBD = BigDecimal.valueOf(amount).setScale(2, BigDecimal.ROUND_HALF_UP);
        if (wallet.getBalance().compareTo(amountBD) < 0) {
            throw new com.kingalzo.l3exam.exception.SoldeInsuffisantException(
                    "Insufficient balance. Available: " + wallet.getBalance() + " CFA, Required: " + amountBD + " CFA");
        }

        strategy.payBill(wallet, amount);
        return walletRepository.save(wallet);
    }

    @Transactional
    public Wallet payMultipleBills(String phoneNumber, String serviceName, List<String> billReferences) {
        Wallet wallet = walletRepository.findByPhone(phoneNumber)
                .orElseThrow(() -> new com.kingalzo.l3exam.exception.WalletNotFoundException("Wallet with phone " + phoneNumber + " not found"));

        BillPaymentStrategy strategy = billPaymentStrategyMap.get(serviceName.toUpperCase());
        if (strategy == null) {
            throw new PaymentServiceNotFoundException("Payment service '" + serviceName + "' not found. Available services: ISM, WOYAFAL");
        }

        // Estimate total amount based on service (for balance check)
        double estimatedTotal = estimateTotalForService(serviceName, billReferences.size());
        BigDecimal estimatedTotalBD = BigDecimal.valueOf(estimatedTotal).setScale(2, BigDecimal.ROUND_HALF_UP);
        
        if (wallet.getBalance().compareTo(estimatedTotalBD) < 0) {
            throw new com.kingalzo.l3exam.exception.SoldeInsuffisantException(
                    "Insufficient balance. Available: " + wallet.getBalance() + " CFA, Required: " + estimatedTotalBD + " CFA");
        }

        strategy.payMultipleBills(wallet, billReferences);
        return walletRepository.save(wallet);
    }

    private double estimateTotalForService(String serviceName, int billCount) {
        return switch (serviceName.toUpperCase()) {
            case "ISM" -> 5000.0 * billCount;
            case "WOYAFAL" -> 3000.0 * billCount;
            default -> 0.0;
        };
    }
}

