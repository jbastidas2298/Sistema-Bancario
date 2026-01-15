package com.example.full_Stack_BP.dto.response;

import com.example.full_Stack_BP.enums.TransactionType;
import lombok.Data;
import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class TransactionResponseDTO {
    private Long id;
    private LocalDateTime date;
    private TransactionType transactionType;
    private BigDecimal amount;
    private BigDecimal balance;
    private String description;
    private AccountResponseDTO account;
}