package com.example.full_Stack_BP.services.impl;

import com.example.full_Stack_BP.domain.Account;
import com.example.full_Stack_BP.domain.Client;
import com.example.full_Stack_BP.dto.request.TransactionRequestDTO;
import com.example.full_Stack_BP.enums.TransactionType;
import com.example.full_Stack_BP.handler.CustomException;
import com.example.full_Stack_BP.repository.AccountRepository;
import com.example.full_Stack_BP.repository.TransactionRepository;
import com.example.full_Stack_BP.services.impl.TransactionServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @Test
    void shouldThrowExceptionWhenDailyWithdrawalLimitExceeded() {
        Client client = Client.builder()
                .id(1L)
                .status(true)
                .build();

        Account account = Account.builder()
                .accountNumber("123456")
                .currentBalance(new BigDecimal("2000"))
                .client(client)
                .status(true)
                .build();

        TransactionRequestDTO request = TransactionRequestDTO.builder()
                .accountNumber("123456")
                .amount(new BigDecimal("300"))
                .transactionType(TransactionType.WITHDRAWAL)
                .build();

        when(accountRepository.findByAccountNumber("123456"))
                .thenReturn(Optional.of(account));

        when(transactionRepository.getTotalDailyWithdrawals(1L))
                .thenReturn(new BigDecimal("800"));

        assertThrows(CustomException.class, () ->
                transactionService.withdraw(request)
        );
    }
}
