package com.example.full_Stack_BP.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum EnumError {
    CLIENT_ALREADY_EXIST("EC001", "El cliente ya existe."),
    CLIENT_ALREADY_EXISTING("EC002", "El cliente ya existe."),
    ACCOUNT_NOT_FOUND("EC003", "La cuenta no fue encontrada."),
    ACCOUNT_INSUFFICIENT_FUNDS("EC004", "Fondos insuficientes en la cuenta."),
    ACCOUNT_ALREADY_EXISTS("EC005", "La cuenta ya existe."),
    CLIENT_NOT_FOUND("EC006", "El cliente no fue encontrado."),
    CLIENT_INACTIVE("EC007", "El cliente está inactivo."),
    INVALID_TRANSACTION("EC008", "Transacción inválida."),
    AMOUNT_MUST_BE_POSITIVE("EC009", "El monto debe ser positivo."),
    REPORT_GENERATION_ERROR("EC010", "Error al generar el reporte."),
    DAILY_WITHDRAWAL_LIMIT_EXCEEDED("EC011", "Se ha excedido el límite diario de retiros."),
    ACCOUNT_HAS_BALANCE("EC012", "La cuenta tiene saldo pendiente.")
    ;

    private final String code;
    private final String description;

}
