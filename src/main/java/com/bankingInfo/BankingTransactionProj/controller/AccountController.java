package com.bankingInfo.BankingTransactionProj.controller;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bankingInfo.BankingTransactionProj.dto.AccountResponse;
import com.bankingInfo.BankingTransactionProj.dto.AmountRequest;
import com.bankingInfo.BankingTransactionProj.dto.CreateAccountRequest;
import com.bankingInfo.BankingTransactionProj.dto.TransferRequest;
import com.bankingInfo.BankingTransactionProj.entity.Account;
import com.bankingInfo.BankingTransactionProj.entity.Transaction;
import com.bankingInfo.BankingTransactionProj.service.AccountService;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    // Create account
    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(
            @Valid @RequestBody CreateAccountRequest request) {

        Account account =
                accountService.createAccount(
                        request.getAccountNumber()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toResponse(account));
    }

    // Get account balance
    @GetMapping("/{accountNumber}/balance")
    public ResponseEntity<BigDecimal> getBalance(
            @PathVariable String accountNumber) {

        BigDecimal balance =
                accountService.getBalance(accountNumber);

        return ResponseEntity.ok(balance);
    }

    // Deposit
    @PostMapping("/{accountNumber}/deposit")
    public ResponseEntity<String> deposit(
            @PathVariable String accountNumber,
            @Valid @RequestBody AmountRequest request) {

        accountService.deposit(
                accountNumber,
                request.getAmount()
        );

        return ResponseEntity.ok(
                "Amount deposited successfully"
        );
    }

    // Withdraw
    @PostMapping("/{accountNumber}/withdraw")
    public ResponseEntity<String> withdraw(
            @PathVariable String accountNumber,
            @Valid @RequestBody AmountRequest request) {

        accountService.withdraw(
                accountNumber,
                request.getAmount()
        );

        return ResponseEntity.ok(
                "Amount withdrawn successfully"
        );
    }

    // Transfer
    @PostMapping("/transfer")
    public ResponseEntity<String> transfer(
            @Valid @RequestBody TransferRequest request) {

        accountService.transfer(
                request.getFromAccountNumber(),
                request.getToAccountNumber(),
                request.getAmount()
        );

        return ResponseEntity.ok(
                "Transfer completed successfully"
        );
    }

    // Transaction history
    @GetMapping("/{accountNumber}/transactions")
    public ResponseEntity<List<Transaction>> getTransactions(
            @PathVariable String accountNumber) {

        List<Transaction> transactions =
                accountService.getTransactionHistory(accountNumber);

        return ResponseEntity.ok(transactions);
    }

    private AccountResponse toResponse(Account account) {

        return new AccountResponse(
                account.getId(),
                account.getAccountNumber(),
                account.getBalance(),
                account.getCreatedAt()
        );
    }
}