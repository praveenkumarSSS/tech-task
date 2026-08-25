package com.bankingInfo.BankingTransactionProj.exception;

public class AccountNotFoundException extends RuntimeException {
	
	public AccountNotFoundException(String message) {
        super(message);
	}

}
