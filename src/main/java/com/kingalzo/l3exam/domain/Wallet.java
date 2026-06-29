package com.kingalzo.l3exam.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "wallets")
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String phone;

    @Column(unique = true)
    private String code;

    @Column
    private String email;

    @Column(nullable = false)
    private String currency = "XOF";

    @Column(nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;

    @OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transaction> transactions = new ArrayList<>();

    public Wallet() {
    }

    public Wallet(String phone, BigDecimal balance) {
        this.phone = phone;
        this.balance = balance;
    }

    public Wallet(String phone, String code, String email, String currency, BigDecimal balance) {
        this.phone = phone;
        this.code = code;
        this.email = email;
        this.currency = currency == null ? "XOF" : currency;
        this.balance = balance;
    }

    public Long getId() {
        return id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void addTransaction(Transaction tx) {
        transactions.add(tx);
        tx.setWallet(this);
        this.balance = this.balance.add(tx.getAmount());
    }

    public void removeTransaction(Transaction tx) {
        if (transactions.remove(tx)) {
            tx.setWallet(null);
            this.balance = this.balance.subtract(tx.getAmount());
        }
    }
}
