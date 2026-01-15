package com.example.full_Stack_BP.repository;

import com.example.full_Stack_BP.domain.Account;
import com.example.full_Stack_BP.domain.Client;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends BaseRepository<Account> {
    Optional<Account> findByAccountNumber(String accountNumber);

    List<Account> findByClient(Client client);

    @Query("SELECT a FROM Account a WHERE a.client = :client AND a.status = true")
    List<Account> findActiveAccountsByClient(@Param("client") Client client);
}
