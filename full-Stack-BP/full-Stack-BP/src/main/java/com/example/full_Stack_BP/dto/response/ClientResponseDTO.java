package com.example.full_Stack_BP.dto.response;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class ClientResponseDTO {
    private Long id;
    private String name;
    private String gender;
    private Integer age;
    private String identification;
    private String address;
    private String phone;
    private Boolean status;
}