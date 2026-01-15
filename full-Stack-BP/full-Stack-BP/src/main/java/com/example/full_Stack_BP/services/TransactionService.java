package com.example.full_Stack_BP.services;

import com.example.full_Stack_BP.dto.request.TransactionRequestDTO;
import com.example.full_Stack_BP.dto.response.TransactionResponseDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface TransactionService {
    TransactionResponseDTO createTransaction(TransactionRequestDTO transactionRequest);
    TransactionResponseDTO deposit(TransactionRequestDTO transactionRequest);
    TransactionResponseDTO withdraw(TransactionRequestDTO transactionRequest);
    TransactionResponseDTO transfer(TransactionRequestDTO transactionRequest);  // Nuevo método
    TransactionResponseDTO getTransactionById(Long id);
    List<TransactionResponseDTO> getTransactionsByAccount(String accountNumber);
    List<TransactionResponseDTO> getTransactionsByClientAndDateRange(
            Long clientId, LocalDateTime startDate, LocalDateTime endDate);
    BigDecimal getDailyWithdrawalTotal(Long clientId);
}