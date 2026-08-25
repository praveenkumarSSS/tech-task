package com.bankingInfo.BankingTransactionProj.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bankingInfo.BankingTransactionProj.entity.Account;

public interface AccountRepository extends JpaRepository<Account,Long> {

	 Optional<Account> findByAccountNumber(String accountNumber);

	 boolean existsByAccountNumber(String accountNumber);
}
