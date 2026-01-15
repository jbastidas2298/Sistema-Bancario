package com.example.full_Stack_BP.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum TransactionType {
    DEPOSIT("Deposito"),
    WITHDRAWAL("Retiro"),
    TRANSFER("Transferencia");

    private final String description;

}
