package com.bankingInfo.BankingTransactionProj.service;



import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.bankingInfo.BankingTransactionProj.entity.Account;
import com.bankingInfo.BankingTransactionProj.entity.Transaction;
import com.bankingInfo.BankingTransactionProj.entity.TransactionType;
import com.bankingInfo.BankingTransactionProj.exception.AccountNotFoundException;
import com.bankingInfo.BankingTransactionProj.exception.InsufficientBalanceException;
import com.bankingInfo.BankingTransactionProj.exception.InvalidAmountException;
import com.bankingInfo.BankingTransactionProj.repository.AccountRepository;
import com.bankingInfo.BankingTransactionProj.repository.TransactionRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public AccountService(
            AccountRepository accountRepository,
            TransactionRepository transactionRepository) {

        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    
    public Account createAccount(String accountNumber) {

        if (accountRepository.existsByAccountNumber(accountNumber)) {
            throw new IllegalArgumentException(
                    "Account already exists: " + accountNumber);
        }

        Account account = new Account(accountNumber);

        return accountRepository.save(account);
    }

    public Account getAccount(String accountNumber) {

        return accountRepository
                .findByAccountNumber(accountNumber)
                .orElseThrow(() ->
                        new AccountNotFoundException(
                                "Account not found: " + accountNumber));
    }

    public BigDecimal getBalance(String accountNumber) {

        Account account = getAccount(accountNumber);

        return account.getBalance();
    }

    @Transactional
    public void deposit(
            String accountNumber,
            BigDecimal amount) {

        validateAmount(amount);

        Account account = getAccount(accountNumber);

        account.setBalance(
                account.getBalance().add(amount)
        );

        accountRepository.save(account);

        Transaction transaction = new Transaction(
                account,
                TransactionType.DEPOSIT,
                amount,
                "Amount deposited"
        );

        transactionRepository.save(transaction);
    }

    
    @Transactional
    public void withdraw(
            String accountNumber,
            BigDecimal amount) {

        validateAmount(amount);

        Account account = getAccount(accountNumber);

        if (account.getBalance().compareTo(amount) < 0) {

            throw new InsufficientBalanceException(
                    "Insufficient balance. Available: "
                            + account.getBalance()
                            + ", Requested: "
                            + amount
            );
        }

        account.setBalance(
                account.getBalance().subtract(amount)
        );

        accountRepository.save(account);

        Transaction transaction = new Transaction(
                account,
                TransactionType.WITHDRAWAL,
                amount,
                "Amount withdrawn"
        );

        transactionRepository.save(transaction);
    }

    @Transactional
    public void transfer(
            String fromAccountNumber,
            String toAccountNumber,
            BigDecimal amount) {

        validateAmount(amount);

        if (fromAccountNumber.equals(toAccountNumber)) {

            throw new IllegalArgumentException(
                    "Sender and receiver cannot be the same account"
            );
        }

        Account fromAccount = getAccount(fromAccountNumber);
        Account toAccount = getAccount(toAccountNumber);

        // Check sender balance
        if (fromAccount.getBalance().compareTo(amount) < 0) {

            throw new InsufficientBalanceException(
                    "Insufficient balance in sender account"
            );
        }

        // Debit sender
        fromAccount.setBalance(
                fromAccount.getBalance().subtract(amount)
        );

        // Credit receiver
        toAccount.setBalance(
                toAccount.getBalance().add(amount)
        );

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        // Sender ledger entry
        Transaction debitTransaction = new Transaction(
                fromAccount,
                TransactionType.TRANSFER_DEBIT,
                amount,
                "Transferred to " + toAccountNumber
        );

        // Receiver ledger entry
        Transaction creditTransaction = new Transaction(
                toAccount,
                TransactionType.TRANSFER_CREDIT,
                amount,
                "Received from " + fromAccountNumber
        );

        transactionRepository.save(debitTransaction);
        transactionRepository.save(creditTransaction);
    }

    
    public List<Transaction> getTransactionHistory(
            String accountNumber) {

        Account account = getAccount(accountNumber);

        return transactionRepository
                .findByAccountOrderByTimestampDesc(account);
    }

    private void validateAmount(BigDecimal amount) {

        if (amount == null ||
                amount.compareTo(BigDecimal.ZERO) <= 0) {

            throw new InvalidAmountException(
                    "Amount must be greater than zero"
            );
        }
    }
}
