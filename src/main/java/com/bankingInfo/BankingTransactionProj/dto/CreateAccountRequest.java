package com.bankingInfo.BankingTransactionProj.dto;


import jakarta.validation.constraints.NotBlank;

public class CreateAccountRequest {

    @NotBlank(message = "Account number is required")
    private String accountNumber;

    public CreateAccountRequest() {
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
}