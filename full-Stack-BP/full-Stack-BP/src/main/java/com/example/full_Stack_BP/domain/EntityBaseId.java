package com.example.full_Stack_BP.domain;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@MappedSuperclass
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("serial")
public abstract class EntityBaseId implements Serializable {
    public static final String ID_PROPERTY = "id";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}