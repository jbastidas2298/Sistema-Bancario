package com.example.full_Stack_BP.handler;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorMessage {
    private final String code;
    private final String message;

    public ErrorMessage(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
