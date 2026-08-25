package com.bankingInfo.BankingTransactionProj.exception;


public class InvalidAmountException extends RuntimeException {

    public InvalidAmountException(String message) {
        super(message);
    }
}