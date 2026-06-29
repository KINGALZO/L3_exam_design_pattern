package com.kingalzo.l3exam.controller;

import com.kingalzo.l3exam.service.WalletSeederService;
import com.kingalzo.l3exam.service.WalletService;
import com.kingalzo.l3exam.dto.WalletCreationRequest;
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
}
