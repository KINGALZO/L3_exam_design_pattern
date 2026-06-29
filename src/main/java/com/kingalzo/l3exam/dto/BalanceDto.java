package com.kingalzo.l3exam.dto;

import java.math.BigDecimal;

public class BalanceDto {
    private BigDecimal balance;

    public BalanceDto() {}

    public BalanceDto(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
