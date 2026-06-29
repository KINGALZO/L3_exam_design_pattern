package com.kingalzo.l3exam.controller;

import com.kingalzo.l3exam.service.WalletSeederService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/wallets")
public class WalletController {

    private final WalletSeederService seederService;

    public WalletController(WalletSeederService seederService) {
        this.seederService = seederService;
    }

    @PostMapping("/seed")
    public ResponseEntity<String> seed(@RequestParam(defaultValue = "100") int count,
                                       @RequestParam(defaultValue = "500") long intervalMillis) {
        seederService.seedAsync(count, intervalMillis);
        return ResponseEntity.accepted().body("Seeding started");
    }
}
