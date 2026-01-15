package com.example.full_Stack_BP.dto.response;

import com.example.full_Stack_BP.enums.TransactionType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ReportResponseDTO {
    private String clientName;
    private String clientIdentification;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<AccountSummary> accounts;
    private BigDecimal totalDeposits;
    private BigDecimal totalWithdrawals;
    private BigDecimal totalTransfers;
    private List<TransactionDetail> transactions;

    @Data
    @Builder
    public static class AccountSummary {
        private String accountNumber;
        private String accountType;
        private BigDecimal initialBalance;
        private BigDecimal currentBalance;
        private Boolean status;
    }

    @Data
    @Builder
    public static class TransactionDetail {
        private LocalDateTime date;
        private String accountNumber;
        private TransactionType transactionType;
        private BigDecimal amount;
        private BigDecimal balance;
        private String description;
        private String destinationAccountNumber; // Para transferencias
    }
}