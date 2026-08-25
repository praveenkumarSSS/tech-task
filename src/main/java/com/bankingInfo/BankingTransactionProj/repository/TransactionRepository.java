package com.bankingInfo.BankingTransactionProj.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bankingInfo.BankingTransactionProj.entity.Account;
import com.bankingInfo.BankingTransactionProj.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
	
    List<Transaction> findByAccountOrderByTimestampDesc(Account account);


}
