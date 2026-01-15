package com.example.full_Stack_BP.controller;

import com.example.full_Stack_BP.dto.response.AccountResponseDTO;
import com.example.full_Stack_BP.services.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
@AutoConfigureMockMvc(addFilters = false)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @Test
    void shouldReturn200WhenGetAccountByIdIsCalled() throws Exception {

        when(accountService.getAccountById(1L))
                .thenReturn(
                        AccountResponseDTO.builder()
                                .id(1L)
                                .accountNumber("123456")
                                .build()
                );

        mockMvc.perform(get("/api/v1/accounts/1"))
                .andExpect(status().isOk());
    }
}
