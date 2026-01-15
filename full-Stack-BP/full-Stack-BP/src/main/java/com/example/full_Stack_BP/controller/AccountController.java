package com.example.full_Stack_BP.controller;

import com.example.full_Stack_BP.dto.request.AccountRequestDTO;
import com.example.full_Stack_BP.dto.response.AccountResponseDTO;
import com.example.full_Stack_BP.services.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Tag(name = "Accounts", description = "Account management endpoints")
@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @Operation(summary = "Create a new account")
    @PostMapping
    public ResponseEntity<AccountResponseDTO> createAccount(
            @RequestBody AccountRequestDTO request) {

        return new ResponseEntity<>(
                accountService.createAccount(request),
                HttpStatus.CREATED
        );
    }

    @Operation(summary = "Get account by id")
    @GetMapping("/{id}")
    public ResponseEntity<AccountResponseDTO> getAccountById(
            @PathVariable Long id) {

        return ResponseEntity.ok(accountService.getAccountById(id));
    }

    @Operation(summary = "Get account by account number")
    @GetMapping("/number/{accountNumber}")
    public ResponseEntity<AccountResponseDTO> getAccountByNumber(
            @PathVariable String accountNumber) {

        return ResponseEntity.ok(
                accountService.getAccountByNumber(accountNumber)
        );
    }

    @Operation(summary = "Get all accounts")
    @GetMapping
    public ResponseEntity<List<AccountResponseDTO>> getAllAccounts() {
        return ResponseEntity.ok(accountService.getAllAccounts());
    }

    @Operation(summary = "Get accounts by client id")
    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<AccountResponseDTO>> getAccountsByClient(
            @PathVariable Long clientId) {

        return ResponseEntity.ok(
                accountService.getAccountsByClient(clientId)
        );
    }

    @Operation(summary = "Update account")
    @PutMapping("/{id}")
    public ResponseEntity<AccountResponseDTO> updateAccount(
            @PathVariable Long id,
            @RequestBody AccountRequestDTO request) {

        return ResponseEntity.ok(
                accountService.updateAccount(id, request)
        );
    }

    @Operation(summary = "Deactivate account")
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<AccountResponseDTO> deactivateAccount(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                accountService.deactivateAccount(id)
        );
    }

    @Operation(summary = "Delete account")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(
            @PathVariable Long id) {

        accountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get account balance")
    @GetMapping("/balance/{accountNumber}")
    public ResponseEntity<BigDecimal> getAccountBalance(
            @PathVariable String accountNumber) {

        return ResponseEntity.ok(
                accountService.getAccountBalance(accountNumber)
        );
    }
}