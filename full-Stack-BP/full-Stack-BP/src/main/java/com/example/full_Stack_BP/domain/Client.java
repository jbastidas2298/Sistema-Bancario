package com.example.full_Stack_BP.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "clients")
@PrimaryKeyJoinColumn(name = "person_id")
public class Client extends Person{
    @NotBlank(message = "Password is required")
    @Size(min = 4, max = 100, message = "Password must be between 4-100 characters")
    @Column(nullable = false, length = 100)
    private String password;

    @NotNull(message = "Status is required")
    @Column(nullable = false)
    private Boolean status;
}
