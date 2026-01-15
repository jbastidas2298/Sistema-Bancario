package com.example.full_Stack_BP.handler;

import com.example.full_Stack_BP.enums.EnumError;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    private final String codigo;

    public CustomException(EnumError error) {
        super(error.getDescription());
        this.codigo = error.getCode();
    }

}

