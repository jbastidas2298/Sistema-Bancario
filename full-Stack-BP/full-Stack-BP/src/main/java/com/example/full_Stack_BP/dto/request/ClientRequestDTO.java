package com.example.full_Stack_BP.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ClientRequestDTO {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Gender is required")
    private String gender;

    @NotNull(message = "Age is required")
    @Min(18)
    @Max(120)
    private Integer age;

    @NotBlank(message = "Identification is required")
    private String identification;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "Phone is required")
    private String phone;


    @NotBlank(message = "Password is required")
    @Size(min = 4, max = 100)
    private String password;

    @NotNull(message = "Status is required")
    private Boolean status;
}
