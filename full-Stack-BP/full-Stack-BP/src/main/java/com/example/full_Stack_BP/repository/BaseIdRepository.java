package com.example.full_Stack_BP.repository;

import com.example.full_Stack_BP.domain.EntityBaseId;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BaseIdRepository<T extends EntityBaseId> extends JpaRepository<T, Long> {

}
