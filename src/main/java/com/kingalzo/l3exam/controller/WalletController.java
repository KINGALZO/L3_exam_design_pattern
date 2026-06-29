package com.kingalzo.l3exam.controller;

import com.kingalzo.l3exam.service.WalletSeederService;
import com.kingalzo.l3exam.service.WalletService;
import com.kingalzo.l3exam.dto.WalletCreationRequest;
import com.kingalzo.l3exam.dto.BillPaymentRequest;
import com.kingalzo.l3exam.dto.MultipleBillPaymentRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/wallets")
public class WalletController {

    private final WalletSeederService seederService;
    private final WalletService walletService;

    public WalletController(WalletSeederService seederService, WalletService walletService) {
        this.seederService = seederService;
        this.walletService = walletService;
    }

    @PostMapping("/seed")
    public ResponseEntity<String> seed(@RequestParam(defaultValue = "100") int count,
                                       @RequestParam(defaultValue = "500") long intervalMillis) {
        seederService.seedAsync(count, intervalMillis);
        return ResponseEntity.accepted().body("Seeding started");
    }

    // Manual wallet creation endpoint
    @PostMapping
    public ResponseEntity<?> createWallet(@RequestBody @Valid WalletCreationRequest request) {
        try {
            com.kingalzo.l3exam.domain.Wallet created = walletService.createWallet(request);
            return ResponseEntity.status(201).body(created);
        } catch (com.kingalzo.l3exam.exception.WalletAlreadyExistsException ex) {
            return ResponseEntity.status(409).body(ex.getMessage());
        }
    }

    // Paginated list of wallets
    @org.springframework.web.bind.annotation.GetMapping
    public ResponseEntity<org.springframework.data.domain.Page<com.kingalzo.l3exam.domain.Wallet>> listWallets(org.springframework.data.domain.Pageable pageable) {
        org.springframework.data.domain.Page<com.kingalzo.l3exam.domain.Wallet> page = walletService.listWallets(pageable);
        return ResponseEntity.ok(page);
    }

    // Get wallet by phone
    @org.springframework.web.bind.annotation.GetMapping("/{phoneNumber}")
    public ResponseEntity<?> getWallet(@org.springframework.web.bind.annotation.PathVariable String phoneNumber) {
        try {
            com.kingalzo.l3exam.domain.Wallet wallet = walletService.getWalletByPhone(phoneNumber);
            return ResponseEntity.ok(wallet);
        } catch (com.kingalzo.l3exam.exception.WalletNotFoundException ex) {
            return ResponseEntity.status(404).body(ex.getMessage());
        }
    }

    // Get only the balance
    @org.springframework.web.bind.annotation.GetMapping("/{phoneNumber}/balance")
    public ResponseEntity<com.kingalzo.l3exam.dto.BalanceDto> getBalance(@org.springframework.web.bind.annotation.PathVariable String phoneNumber) {
        java.math.BigDecimal balance = walletService.getBalanceByPhone(phoneNumber);
        return ResponseEntity.ok(new com.kingalzo.l3exam.dto.BalanceDto(balance));
    }

    // Deposit endpoint with strategy pattern
    @PostMapping("/{id}/deposit")
    public ResponseEntity<?> deposit(@org.springframework.web.bind.annotation.PathVariable Long id,
                                     @RequestBody @Valid com.kingalzo.l3exam.dto.DepositRequest request) {
        try {
            com.kingalzo.l3exam.domain.Wallet updated = walletService.executeDeposit(id, request.getPaymentMethod(), request.getAmount());
            return ResponseEntity.ok(updated);
        } catch (com.kingalzo.l3exam.exception.WalletNotFoundException ex) {
            return ResponseEntity.status(404).body(ex.getMessage());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(400).body(ex.getMessage());
        }
    }

    // Withdrawal endpoint
    @PostMapping("/withdraw")
    public ResponseEntity<?> withdraw(@RequestBody @Valid com.kingalzo.l3exam.dto.WithdrawalRequest request) {
        try {
            com.kingalzo.l3exam.domain.Wallet updated = walletService.withdraw(request.getPhoneNumber(), request.getAmount());
            return ResponseEntity.ok(updated);
        } catch (com.kingalzo.l3exam.exception.WalletNotFoundException ex) {
            return ResponseEntity.status(404).body(ex.getMessage());
        } catch (com.kingalzo.l3exam.exception.SoldeInsuffisantException ex) {
            return ResponseEntity.status(400).body(ex.getMessage());
        }
    }

    // Transfer endpoint
    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(@RequestBody @Valid com.kingalzo.l3exam.dto.TransferRequest request) {
        try {
            walletService.transfer(request.getSenderPhone(), request.getReceiverPhone(), request.getAmount());
            return ResponseEntity.status(200).body("Transfer successful");
        } catch (com.kingalzo.l3exam.exception.WalletNotFoundException ex) {
            return ResponseEntity.status(404).body(ex.getMessage());
        } catch (com.kingalzo.l3exam.exception.SoldeInsuffisantException ex) {
            return ResponseEntity.status(400).body(ex.getMessage());
        }
    }

    // Single bill payment endpoint
    @PostMapping("/pay")
    public ResponseEntity<?> paySingleBill(@RequestBody @Valid BillPaymentRequest request) {
        try {
            com.kingalzo.l3exam.domain.Wallet updated = walletService.paySingleBill(
                    request.getPhoneNumber(), 
                    request.getServiceName(), 
                    request.getAmount());
            return ResponseEntity.ok(updated);
        } catch (com.kingalzo.l3exam.exception.WalletNotFoundException ex) {
            return ResponseEntity.status(404).body(ex.getMessage());
        } catch (com.kingalzo.l3exam.exception.PaymentServiceNotFoundException ex) {
            return ResponseEntity.status(400).body(ex.getMessage());
        } catch (com.kingalzo.l3exam.exception.SoldeInsuffisantException ex) {
            return ResponseEntity.status(400).body(ex.getMessage());
        }
    }

    // Multiple bills payment endpoint
    @PostMapping("/pay-factures")
    public ResponseEntity<?> payMultipleBills(@RequestBody @Valid MultipleBillPaymentRequest request) {
        try {
            com.kingalzo.l3exam.domain.Wallet updated = walletService.payMultipleBills(
                    request.getPhoneNumber(),
                    request.getServiceName(),
                    request.getBillReferences());
            return ResponseEntity.ok(updated);
        } catch (com.kingalzo.l3exam.exception.WalletNotFoundException ex) {
            return ResponseEntity.status(404).body(ex.getMessage());
        } catch (com.kingalzo.l3exam.exception.PaymentServiceNotFoundException ex) {
            return ResponseEntity.status(400).body(ex.getMessage());
        } catch (com.kingalzo.l3exam.exception.SoldeInsuffisantException ex) {
            return ResponseEntity.status(400).body(ex.getMessage());
        }
    }
}
