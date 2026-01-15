package com.example.full_Stack_BP.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum AccountType {
    SAVINGS("Ahorros"),
    CURRENT("Corriente");
    private final String description;
}
