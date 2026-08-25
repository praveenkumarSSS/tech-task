package com.bankingInfo.BankingTransactionProj.dto;


import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AccountResponse {

    private Long id;
    private String accountNumber;
    private BigDecimal balance;
    private LocalDateTime createdAt;

    public AccountResponse() {
    }

    public AccountResponse(
            Long id,
            String accountNumber,
            BigDecimal balance,
            LocalDateTime createdAt) {

        this.id = id;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
