package com.example.full_Stack_BP.dto.response;

import com.example.full_Stack_BP.enums.AccountType;
import lombok.Data;
import lombok.Builder;
import java.math.BigDecimal;

@Data
@Builder
public class AccountResponseDTO {
    private Long id;
    private String accountNumber;
    private AccountType accountType;
    private BigDecimal initialBalance;
    private BigDecimal currentBalance;
    private Boolean status;
    private ClientResponseDTO client;
}