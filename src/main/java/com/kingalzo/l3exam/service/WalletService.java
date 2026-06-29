package com.kingalzo.l3exam.service;

import com.kingalzo.l3exam.domain.Wallet;
import com.kingalzo.l3exam.dto.WalletCreationRequest;
import com.kingalzo.l3exam.exception.WalletAlreadyExistsException;
import com.kingalzo.l3exam.repository.WalletRepository;
import com.kingalzo.l3exam.strategy.DepositStrategy;
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

    public WalletService(WalletRepository walletRepository, List<DepositStrategy> strategies) {
        this.walletRepository = walletRepository;
        this.strategyMap = strategies.stream()
                .collect(Collectors.toMap(DepositStrategy::getStrategyName, strategy -> strategy));
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
}
