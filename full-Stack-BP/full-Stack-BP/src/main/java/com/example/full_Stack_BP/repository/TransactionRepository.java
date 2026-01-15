package com.example.full_Stack_BP.repository;

import com.example.full_Stack_BP.domain.Account;
import com.example.full_Stack_BP.domain.Transaction;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends BaseRepository<Transaction> {

    List<Transaction> findByAccountOrderByDateDesc(Account account);

    @Query("SELECT t FROM Transaction t " +
            "WHERE t.account.client.id = :clientId " +
            "AND t.date BETWEEN :startDate AND :endDate " +
            "ORDER BY t.date DESC")
    List<Transaction> findByClientAndDateRange(
            @Param("clientId") Long clientId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
            "WHERE t.account.client.id = :clientId " +
            "AND CAST(t.date AS date) = CURRENT_DATE " +
            "AND t.transactionType = 'WITHDRAWAL'")
    BigDecimal getTotalDailyWithdrawals(@Param("clientId") Long clientId);
}