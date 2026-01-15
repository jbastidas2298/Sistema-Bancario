package com.example.full_Stack_BP.dto.request;

import com.example.full_Stack_BP.enums.AccountType;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class AccountRequestDTO {

    @NotBlank(message = "Account number is required")
    private String accountNumber;

    @NotBlank(message = "Account type is required")
    private AccountType accountType;

    @NotNull(message = "Initial balance is required")
    @DecimalMin("0.00")
    private BigDecimal initialBalance;

    @NotNull(message = "Client ID is required")
    private Long clientId;

    @NotNull(message = "Status is required")
    private Boolean status;
}