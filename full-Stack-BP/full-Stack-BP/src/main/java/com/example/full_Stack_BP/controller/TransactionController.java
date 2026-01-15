package com.example.full_Stack_BP.controller;


import com.example.full_Stack_BP.dto.request.TransactionRequestDTO;
import com.example.full_Stack_BP.dto.response.TransactionResponseDTO;
import com.example.full_Stack_BP.services.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Transactions", description = "Transaction management endpoints")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    @Operation(summary = "Create a new transaction (deposit, withdrawal or transfer)")
    public ResponseEntity<TransactionResponseDTO> createTransaction(
            @Valid @RequestBody TransactionRequestDTO transactionRequest) {
        log.info("Received request to create {} transaction for account: {}",
                transactionRequest.getTransactionType(), transactionRequest.getAccountNumber());
        TransactionResponseDTO transaction = transactionService.createTransaction(transactionRequest);
        log.info("Transaction created successfully with id: {}", transaction.getId());
        return new ResponseEntity<>(transaction, HttpStatus.CREATED);
    }

    @PostMapping("/deposit")
    @Operation(summary = "Create a deposit")
    public ResponseEntity<TransactionResponseDTO> deposit(
            @Valid @RequestBody TransactionRequestDTO transactionRequest) {
        log.info("Received request to deposit to account: {}", transactionRequest.getAccountNumber());
        TransactionResponseDTO transaction = transactionService.deposit(transactionRequest);
        log.info("Deposit completed successfully with id: {}", transaction.getId());
        return new ResponseEntity<>(transaction, HttpStatus.CREATED);
    }

    @PostMapping("/withdraw")
    @Operation(summary = "Create a withdrawal")
    public ResponseEntity<TransactionResponseDTO> withdraw(
            @Valid @RequestBody TransactionRequestDTO transactionRequest) {
        log.info("Received request to withdraw from account: {}", transactionRequest.getAccountNumber());
        TransactionResponseDTO transaction = transactionService.withdraw(transactionRequest);
        log.info("Withdrawal completed successfully with id: {}", transaction.getId());
        return new ResponseEntity<>(transaction, HttpStatus.CREATED);
    }

    @PostMapping("/transfer")
    @Operation(summary = "Create a transfer between accounts")
    public ResponseEntity<TransactionResponseDTO> transfer(
            @Valid @RequestBody TransactionRequestDTO transactionRequest) {
        log.info("Received request to transfer from account: {} to account: {}",
                transactionRequest.getAccountNumber(), transactionRequest.getDestinationAccountNumber());
        TransactionResponseDTO transaction = transactionService.transfer(transactionRequest);
        log.info("Transfer completed successfully with id: {}", transaction.getId());
        return new ResponseEntity<>(transaction, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get transaction by ID")
    public ResponseEntity<TransactionResponseDTO> getTransactionById(@PathVariable Long id) {
        log.info("Received request to get transaction by id: {}", id);
        TransactionResponseDTO transaction = transactionService.getTransactionById(id);
        log.info("Transaction retrieved successfully for id: {}", id);
        return ResponseEntity.ok(transaction);
    }

    @GetMapping("/account/{accountNumber}")
    @Operation(summary = "Get transactions by account")
    public ResponseEntity<List<TransactionResponseDTO>> getTransactionsByAccount(
            @PathVariable String accountNumber) {
        log.info("Received request to get transactions for account: {}", accountNumber);
        List<TransactionResponseDTO> transactions = transactionService.getTransactionsByAccount(accountNumber);
        log.info("Retrieved {} transactions for account: {}", transactions.size(), accountNumber);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/client/{clientId}")
    @Operation(summary = "Get transactions by client and date range")
    public ResponseEntity<List<TransactionResponseDTO>> getTransactionsByClientAndDateRange(
            @PathVariable Long clientId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        log.info("Received request to get transactions for client {} from {} to {}",
                clientId, startDate, endDate);
        List<TransactionResponseDTO> transactions = transactionService
                .getTransactionsByClientAndDateRange(clientId, startDate, endDate);
        log.info("Retrieved {} transactions for client {}", transactions.size(), clientId);
        return ResponseEntity.ok(transactions);
    }
}