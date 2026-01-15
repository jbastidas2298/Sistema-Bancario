package com.example.full_Stack_BP.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class EntityBase extends EntityBaseId {
    public static final String CREATION_DATE_PROPERTY = "creationDate";
    public static final String MODIFICATION_DATE_PROPERTY = "modificationDate";

    @CreatedDate
    @JsonIgnore
    private LocalDateTime creationDate;

    @CreatedBy
    @NotNull
    @JsonIgnore
    private String createdBy;

    @LastModifiedDate
    @JsonIgnore
    private LocalDateTime modificationDate;

    @LastModifiedBy
    @NotNull
    @JsonIgnore
    private String modifiedBy;
}