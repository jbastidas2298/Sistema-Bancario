package com.example.full_Stack_BP.services;

import com.example.full_Stack_BP.dto.request.AccountRequestDTO;
import com.example.full_Stack_BP.dto.response.AccountResponseDTO;

import java.math.BigDecimal;
import java.util.List;

public interface AccountService {
    AccountResponseDTO createAccount(AccountRequestDTO accountRequest);
    AccountResponseDTO getAccountById(Long id);
    AccountResponseDTO getAccountByNumber(String accountNumber);
    List<AccountResponseDTO> getAllAccounts();
    List<AccountResponseDTO> getAccountsByClient(Long clientId);
    AccountResponseDTO updateAccount(Long id, AccountRequestDTO accountRequest);
    void deleteAccount(Long id);
    AccountResponseDTO deactivateAccount(Long id);
    BigDecimal getAccountBalance(String accountNumber);
}