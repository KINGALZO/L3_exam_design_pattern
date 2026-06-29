package com.kingalzo.l3exam.service;

import com.kingalzo.l3exam.domain.Wallet;
import com.kingalzo.l3exam.dto.WalletCreationRequest;
import com.kingalzo.l3exam.exception.WalletAlreadyExistsException;
import com.kingalzo.l3exam.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class WalletService {

    private final WalletRepository walletRepository;

    public WalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
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
}
