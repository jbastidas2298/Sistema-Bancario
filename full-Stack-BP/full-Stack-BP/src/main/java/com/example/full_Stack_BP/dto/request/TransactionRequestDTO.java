package com.example.full_Stack_BP.dto.request;
import com.example.full_Stack_BP.enums.TransactionType;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class TransactionRequestDTO {

    @NotNull(message = "Account number is required")
    private String accountNumber;

    @NotNull(message = "Transaction type is required")
    private TransactionType transactionType;

    @NotNull(message = "Amount is required")
    @DecimalMin("0.01")
    private BigDecimal amount;

    @NotBlank(message = "Description is required")
    private String description;

    private String destinationAccountNumber;
}