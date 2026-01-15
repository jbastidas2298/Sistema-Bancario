package com.example.full_Stack_BP.services.impl;

import com.example.full_Stack_BP.domain.Account;
import com.example.full_Stack_BP.domain.Transaction;
import com.example.full_Stack_BP.dto.request.TransactionRequestDTO;
import com.example.full_Stack_BP.dto.response.AccountResponseDTO;
import com.example.full_Stack_BP.dto.response.TransactionResponseDTO;
import com.example.full_Stack_BP.enums.EnumError;
import com.example.full_Stack_BP.enums.TransactionType;
import com.example.full_Stack_BP.exception.BusinessException;
import com.example.full_Stack_BP.exception.ResourceNotFoundException;
import com.example.full_Stack_BP.handler.CustomException;
import com.example.full_Stack_BP.repository.AccountRepository;
import com.example.full_Stack_BP.repository.TransactionRepository;
import com.example.full_Stack_BP.services.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    private static final BigDecimal DAILY_WITHDRAWAL_LIMIT = new BigDecimal("1000");
    private static final BigDecimal ZERO = BigDecimal.ZERO;

    @Override
    public TransactionResponseDTO deposit(TransactionRequestDTO transactionRequest) {
        log.info("Processing deposit request for account: {}", transactionRequest.getAccountNumber());

        Account account = getActiveAccount(transactionRequest.getAccountNumber());
        validateDeposit(transactionRequest, account);

        BigDecimal amount = transactionRequest.getAmount();
        BigDecimal newBalance = account.getCurrentBalance().add(amount);

        Transaction transaction = createTransaction(
                account,
                TransactionType.DEPOSIT,
                amount,
                newBalance,
                transactionRequest.getDescription()
        );

        account.setCurrentBalance(newBalance);
        accountRepository.save(account);

        Transaction savedTransaction = transactionRepository.save(transaction);
        log.info("Deposit completed. Transaction id: {}, Account: {}, Amount: {}, New Balance: {}",
                savedTransaction.getId(), account.getAccountNumber(), amount, newBalance);

        return mapToResponseDTO(savedTransaction);
    }

    @Override
    public TransactionResponseDTO withdraw(TransactionRequestDTO transactionRequest) {
        log.info("Processing withdrawal request for account: {}", transactionRequest.getAccountNumber());

        Account account = getActiveAccount(transactionRequest.getAccountNumber());
        validateWithdrawal(transactionRequest, account);

        BigDecimal amount = transactionRequest.getAmount();
        BigDecimal newBalance = account.getCurrentBalance().subtract(amount);

        Transaction transaction = createTransaction(
                account,
                TransactionType.WITHDRAWAL,
                amount,
                newBalance,
                transactionRequest.getDescription()
        );

        account.setCurrentBalance(newBalance);
        accountRepository.save(account);

        Transaction savedTransaction = transactionRepository.save(transaction);
        log.info("Withdrawal completed. Transaction id: {}, Account: {}, Amount: {}, New Balance: {}",
                savedTransaction.getId(), account.getAccountNumber(), amount, newBalance);

        return mapToResponseDTO(savedTransaction);
    }

    @Override
    public TransactionResponseDTO transfer(TransactionRequestDTO transactionRequest) {
        log.info("Processing transfer request from account: {}", transactionRequest.getAccountNumber());

        if (transactionRequest.getDestinationAccountNumber() == null) {
            log.error("Destination account number is required for transfer");
            throw new CustomException(EnumError.INVALID_TRANSACTION);
        }
        Account sourceAccount = getActiveAccount(transactionRequest.getAccountNumber());

        Account destinationAccount = accountRepository
                .findByAccountNumber(transactionRequest.getDestinationAccountNumber())
                .orElseThrow(() -> {
                    log.error("Destination account not found: {}",
                            transactionRequest.getDestinationAccountNumber());
                    return new CustomException(EnumError.ACCOUNT_NOT_FOUND);
                });

        if (!destinationAccount.getStatus()) {
            log.error("Destination account {} is inactive", destinationAccount.getAccountNumber());
            throw new CustomException(EnumError.ACCOUNT_NOT_FOUND);
        }

        if (!destinationAccount.getClient().getStatus()) {
            log.error("Destination client {} is inactive", destinationAccount.getClient().getId());
            throw new CustomException(EnumError.CLIENT_INACTIVE);
        }

        validateWithdrawal(transactionRequest, sourceAccount);

        BigDecimal amount = transactionRequest.getAmount();

        BigDecimal sourceNewBalance = sourceAccount.getCurrentBalance().subtract(amount);

        Transaction sourceTransaction = createTransaction(
                sourceAccount,
                TransactionType.TRANSFER,
                amount,
                sourceNewBalance,
                "Transfer to account: " + destinationAccount.getAccountNumber() +
                        " - " + transactionRequest.getDescription()
        );

        sourceAccount.setCurrentBalance(sourceNewBalance);
        accountRepository.save(sourceAccount);
        transactionRepository.save(sourceTransaction);

        BigDecimal destinationNewBalance = destinationAccount.getCurrentBalance().add(amount);

        Transaction destinationTransaction = createTransaction(
                destinationAccount,
                TransactionType.TRANSFER,
                amount,
                destinationNewBalance,
                "Transfer from account: " + sourceAccount.getAccountNumber() +
                        " - " + transactionRequest.getDescription()
        );

        destinationAccount.setCurrentBalance(destinationNewBalance);
        accountRepository.save(destinationAccount);
        Transaction savedDestinationTransaction = transactionRepository.save(destinationTransaction);

        log.info("Transfer completed. From: {}, To: {}, Amount: {}, Source New Balance: {}, Destination New Balance: {}",
                sourceAccount.getAccountNumber(), destinationAccount.getAccountNumber(),
                amount, sourceNewBalance, destinationNewBalance);

        return mapToResponseDTO(savedDestinationTransaction);
    }

    @Override
    public TransactionResponseDTO createTransaction(TransactionRequestDTO transactionRequest) {
        log.info("Creating transaction for account: {}", transactionRequest.getAccountNumber());

        switch (transactionRequest.getTransactionType()) {
            case DEPOSIT:
                return deposit(transactionRequest);
            case WITHDRAWAL:
                return withdraw(transactionRequest);
            case TRANSFER:
                return transfer(transactionRequest);
            default:
                log.error("Invalid transaction type: {}", transactionRequest.getTransactionType());
                throw new CustomException(EnumError.INVALID_TRANSACTION);
        }
    }

    @Override
    public TransactionResponseDTO getTransactionById(Long id) {
        log.info("Retrieving transaction with id: {}", id);
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Transaction not found with id: {}", id);
                    return new CustomException(EnumError.INVALID_TRANSACTION);
                });
        log.info("Transaction with id {} retrieved successfully", id);
        return mapToResponseDTO(transaction);
    }

    @Override
    public List<TransactionResponseDTO> getTransactionsByAccount(String accountNumber) {
        log.info("Retrieving transactions for account: {}", accountNumber);
        Account account = getActiveAccount(accountNumber);

        return transactionRepository.findByAccountOrderByDateDesc(account).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TransactionResponseDTO> getTransactionsByClientAndDateRange(
            Long clientId, LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Retrieving transactions for client id: {} between {} and {}",
                clientId, startDate, endDate);

        return transactionRepository.findByClientAndDateRange(clientId, startDate, endDate).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public BigDecimal getDailyWithdrawalTotal(Long clientId) {
        log.info("Getting daily withdrawal total for client id: {}", clientId);
        return transactionRepository.getTotalDailyWithdrawals(clientId);
    }

    private Account getActiveAccount(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> {
                    log.error("Account not found with number: {}", accountNumber);
                    return new CustomException(EnumError.ACCOUNT_NOT_FOUND);
                });

        if (!account.getStatus()) {
            log.error("Account {} is inactive", accountNumber);
            throw new CustomException(EnumError.ACCOUNT_NOT_FOUND);
        }

        if (!account.getClient().getStatus()) {
            log.error("Client {} is inactive for account {}",
                    account.getClient().getId(), accountNumber);
            throw new CustomException(EnumError.CLIENT_INACTIVE);
        }

        return account;
    }

    private void validateDeposit(TransactionRequestDTO request, Account account) {
        log.debug("Validating deposit for account: {}", account.getAccountNumber());

        if (request.getAmount().compareTo(ZERO) <= 0) {
            log.error("Deposit amount must be greater than zero. Amount: {}", request.getAmount());
            throw new CustomException(EnumError.AMOUNT_MUST_BE_POSITIVE);
        }
    }

    private void validateWithdrawal(TransactionRequestDTO request, Account account) {
        log.debug("Validating withdrawal for account: {}", account.getAccountNumber());

        if (request.getAmount().compareTo(ZERO) <= 0) {
            log.error("Withdrawal amount must be greater than zero. Amount: {}", request.getAmount());
            throw new CustomException(EnumError.AMOUNT_MUST_BE_POSITIVE);
        }

        if (account.getCurrentBalance().compareTo(request.getAmount()) < 0) {
            log.error("Insufficient balance. Current: {}, Requested: {}",
                    account.getCurrentBalance(), request.getAmount());
            throw new CustomException(EnumError.ACCOUNT_INSUFFICIENT_FUNDS);
        }

        if (request.getTransactionType() == TransactionType.WITHDRAWAL) {
            BigDecimal dailyWithdrawals = getDailyWithdrawalTotal(account.getClient().getId());
            BigDecimal projectedTotal = dailyWithdrawals.add(request.getAmount());

            if (projectedTotal.compareTo(DAILY_WITHDRAWAL_LIMIT) > 0) {
                log.error("Daily withdrawal limit exceeded. Limit: {}, Daily Total: {}, Requested: {}, Projected: {}",
                        DAILY_WITHDRAWAL_LIMIT, dailyWithdrawals, request.getAmount(), projectedTotal);
                throw new CustomException(EnumError.DAILY_WITHDRAWAL_LIMIT_EXCEEDED);
            }
        }
    }

    private Transaction createTransaction(Account account, TransactionType type,
                                          BigDecimal amount, BigDecimal newBalance, String description) {
        return Transaction.builder()
                .date(LocalDateTime.now())
                .transactionType(type)
                .amount(amount)
                .balance(newBalance)
                .description(description)
                .account(account)
                .build();
    }

    private TransactionResponseDTO mapToResponseDTO(Transaction transaction) {
        log.debug("Mapping Transaction entity to TransactionResponseDTO for transaction id {}",
                transaction.getId());

        BigDecimal responseAmount = transaction.getAmount();
        if (transaction.getTransactionType() == TransactionType.WITHDRAWAL ||
                (transaction.getTransactionType() == TransactionType.TRANSFER &&
                        transaction.getAmount().compareTo(ZERO) < 0)) {
            responseAmount = responseAmount.abs();
        }

        return TransactionResponseDTO.builder()
                .id(transaction.getId())
                .date(transaction.getDate())
                .transactionType(transaction.getTransactionType())
                .amount(responseAmount)
                .balance(transaction.getBalance())
                .description(transaction.getDescription())
                .account(AccountResponseDTO.builder()
                        .id(transaction.getAccount().getId())
                        .accountNumber(transaction.getAccount().getAccountNumber())
                        .accountType(transaction.getAccount().getAccountType())
                        .currentBalance(transaction.getAccount().getCurrentBalance())
                        .status(transaction.getAccount().getStatus())
                        .build())
                .build();
    }
}