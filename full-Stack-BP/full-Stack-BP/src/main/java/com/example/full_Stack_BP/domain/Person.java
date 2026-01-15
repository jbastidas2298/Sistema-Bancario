package com.example.full_Stack_BP.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "persons")
@Inheritance(strategy = InheritanceType.JOINED)
public class Person extends EntityBase {
    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name cannot exceed 100 characters")
    @Column(nullable = false, length = 100)
    private String name;

    @NotBlank(message = "Gender is required")
    @Size(max = 20, message = "Gender cannot exceed 20 characters")
    @Column(nullable = false, length = 20)
    private String gender;

    @NotNull(message = "Age is required")
    @Min(value = 18, message = "Minimum age is 18")
    @Max(value = 120, message = "Maximum age is 120")
    @Column(nullable = false)
    private Integer age;

    @NotBlank(message = "Identification is required")
    @Size(max = 20, message = "Identification cannot exceed 20 characters")
    @Column(nullable = false, unique = true, length = 20)
    private String identification;

    @NotBlank(message = "Address is required")
    @Size(max = 200, message = "Address cannot exceed 200 characters")
    @Column(nullable = false, length = 200)
    private String address;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^[0-9]{10,15}$", message = "Phone must have 10-15 digits")
    @Column(nullable = false, length = 15)
    private String phone;
}
