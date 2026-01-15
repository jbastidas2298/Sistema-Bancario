package com.example.full_Stack_BP.controller;

import com.example.full_Stack_BP.dto.request.TransactionRequestDTO;
import com.example.full_Stack_BP.dto.response.AccountResponseDTO;
import com.example.full_Stack_BP.dto.response.TransactionResponseDTO;
import com.example.full_Stack_BP.enums.TransactionType;
import com.example.full_Stack_BP.services.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransactionController.class)
@AutoConfigureMockMvc(addFilters = false)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturn201WhenWithdrawEndpointIsCalled() throws Exception {

        TransactionRequestDTO request = TransactionRequestDTO.builder()
                .accountNumber("123456")
                .amount(BigDecimal.valueOf(200))
                .transactionType(TransactionType.WITHDRAWAL)
                .description("PRB Test")
                .build();

        when(transactionService.withdraw(any()))
                .thenReturn(
                        TransactionResponseDTO.builder()
                                .id(1L)
                                .date(LocalDateTime.now())
                                .transactionType(TransactionType.WITHDRAWAL)
                                .amount(BigDecimal.valueOf(200))
                                .balance(BigDecimal.valueOf(1800))
                                .description("PRB Test")
                                .account(
                                        AccountResponseDTO.builder()
                                                .id(10L)
                                                .accountNumber("123456")
                                                .build()
                                )
                                .build()
                );
        mockMvc.perform(post("/api/v1/transactions/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }
}
