package com.example.full_Stack_BP.services.impl;

import com.example.full_Stack_BP.domain.Account;
import com.example.full_Stack_BP.domain.Client;
import com.example.full_Stack_BP.dto.request.AccountRequestDTO;
import com.example.full_Stack_BP.dto.response.AccountResponseDTO;
import com.example.full_Stack_BP.dto.response.ClientResponseDTO;
import com.example.full_Stack_BP.enums.EnumError;
import com.example.full_Stack_BP.exception.ResourceNotFoundException;
import com.example.full_Stack_BP.handler.CustomException;
import com.example.full_Stack_BP.repository.AccountRepository;
import com.example.full_Stack_BP.repository.ClientRepository;
import com.example.full_Stack_BP.services.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final ClientRepository clientRepository;

    @Override
    public AccountResponseDTO createAccount(AccountRequestDTO accountRequest) {
        log.info("Creating new account with number: {}", accountRequest.getAccountNumber());

        accountRepository.findByAccountNumber(accountRequest.getAccountNumber())
                .ifPresent(account -> {
                    log.error("Account with number {} already exists",
                            accountRequest.getAccountNumber());
                    throw new CustomException(EnumError.ACCOUNT_ALREADY_EXISTS);
                });

        Client client = clientRepository.findById(accountRequest.getClientId())
                .orElseThrow(() -> {
                    log.error("Client not found with id: {}", accountRequest.getClientId());
                    return new CustomException(EnumError.CLIENT_NOT_FOUND);
                });

        if (!client.getStatus()) {
            log.error("Client with id {} is inactive", client.getId());
            throw new CustomException(EnumError.CLIENT_INACTIVE);
        }

        Account account = Account.builder()
                .accountNumber(accountRequest.getAccountNumber())
                .accountType(accountRequest.getAccountType())
                .initialBalance(accountRequest.getInitialBalance())
                .currentBalance(accountRequest.getInitialBalance())
                .status(accountRequest.getStatus() != null ? accountRequest.getStatus() : true)
                .client(client)
                .build();

        Account savedAccount = accountRepository.save(account);
        log.info("Account with id {} created successfully for client {}",
                savedAccount.getId(), client.getId());
        return mapToResponseDTO(savedAccount);
    }

    @Override
    public AccountResponseDTO getAccountById(Long id) {
        log.info("Retrieving account with id: {}", id);
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Account not found with id: {}", id);
                    return new CustomException(EnumError.ACCOUNT_NOT_FOUND);
                });
        log.info("Account with id {} retrieved successfully", id);
        return mapToResponseDTO(account);
    }

    @Override
    public AccountResponseDTO getAccountByNumber(String accountNumber) {
        log.info("Retrieving account with number: {}", accountNumber);
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> {
                    log.error("Account not found with number: {}", accountNumber);
                    return new CustomException(EnumError.ACCOUNT_NOT_FOUND);
                });
        log.info("Account with number {} retrieved successfully", accountNumber);
        return mapToResponseDTO(account);
    }

    @Override
    public List<AccountResponseDTO> getAllAccounts() {
        log.info("Retrieving all accounts");
        return accountRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AccountResponseDTO> getAccountsByClient(Long clientId) {
        log.info("Retrieving accounts for client id: {}", clientId);
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> {
                    log.error("Client not found with id: {}", clientId);
                    return new CustomException(EnumError.CLIENT_NOT_FOUND);
                });

        return accountRepository.findByClient(client).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AccountResponseDTO updateAccount(Long id, AccountRequestDTO accountRequest) {
        log.info("Updating account with id: {}", id);
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Account not found with id: {}", id);
                    return new CustomException(EnumError.ACCOUNT_NOT_FOUND);
                });

        if (!account.getAccountNumber().equals(accountRequest.getAccountNumber())) {
            accountRepository.findByAccountNumber(accountRequest.getAccountNumber())
                    .ifPresent(existingAccount -> {
                        log.error("Account number {} already exists",
                                accountRequest.getAccountNumber());
                        throw new CustomException(EnumError.ACCOUNT_ALREADY_EXISTS);
                    });
        }

        Client client = clientRepository.findById(accountRequest.getClientId())
                .orElseThrow(() -> {
                    log.error("Client not found with id: {}", accountRequest.getClientId());
                    return new CustomException(EnumError.CLIENT_NOT_FOUND);
                });

        account.setAccountNumber(accountRequest.getAccountNumber());
        account.setAccountType(accountRequest.getAccountType());
        account.setInitialBalance(accountRequest.getInitialBalance());
        account.setStatus(accountRequest.getStatus());
        account.setClient(client);

        Account updatedAccount = accountRepository.save(account);
        log.info("Account with id {} updated successfully", id);
        return mapToResponseDTO(updatedAccount);
    }

    @Override
    public void deleteAccount(Long id) {
        log.info("Deleting account with id: {}", id);
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Account not found with id: {}", id);
                    return new CustomException(EnumError.ACCOUNT_NOT_FOUND);
                });

        if (account.getCurrentBalance().compareTo(BigDecimal.ZERO) != 0) {
            log.error("Cannot delete account with id {} because it has balance: {}",
                    id, account.getCurrentBalance());
            throw new CustomException(EnumError.ACCOUNT_HAS_BALANCE);
        }

        accountRepository.delete(account);
        log.info("Account with id {} deleted successfully", id);
    }

    @Override
    public AccountResponseDTO deactivateAccount(Long id) {
        log.info("Deactivating account with id: {}", id);
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Account not found with id: {}", id);
                    return new CustomException(EnumError.ACCOUNT_NOT_FOUND);
                });

        account.setStatus(false);
        Account updatedAccount = accountRepository.save(account);
        log.info("Account with id {} deactivated successfully", id);
        return mapToResponseDTO(updatedAccount);
    }

    @Override
    public BigDecimal getAccountBalance(String accountNumber) {
        log.info("Getting balance for account number: {}", accountNumber);
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> {
                    log.error("Account not found with number: {}", accountNumber);
                    return new CustomException(EnumError.ACCOUNT_NOT_FOUND);
                });

        if (!account.getStatus()) {
            log.error("Account {} is inactive", accountNumber);
            throw new CustomException(EnumError.ACCOUNT_NOT_FOUND);
        }

        log.info("Balance for account {}: {}", accountNumber, account.getCurrentBalance());
        return account.getCurrentBalance();
    }

    private AccountResponseDTO mapToResponseDTO(Account account) {
        log.debug("Mapping Account entity to AccountResponseDTO for account id {}", account.getId());
        return AccountResponseDTO.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .accountType(account.getAccountType())
                .initialBalance(account.getInitialBalance())
                .currentBalance(account.getCurrentBalance())
                .status(account.getStatus())
                .client(ClientResponseDTO.builder()
                        .id(account.getClient().getId())
                        .name(account.getClient().getName())
                        .identification(account.getClient().getIdentification())
                        .status(account.getClient().getStatus())
                        .build())
                .build();
    }
}