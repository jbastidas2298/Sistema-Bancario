package com.example.full_Stack_BP.services.impl;

import com.example.full_Stack_BP.domain.Account;
import com.example.full_Stack_BP.domain.Client;
import com.example.full_Stack_BP.domain.Transaction;
import com.example.full_Stack_BP.dto.response.ReportResponseDTO;
import com.example.full_Stack_BP.enums.EnumError;
import com.example.full_Stack_BP.enums.TransactionType;
import com.example.full_Stack_BP.exception.ResourceNotFoundException;
import com.example.full_Stack_BP.handler.CustomException;
import com.example.full_Stack_BP.repository.AccountRepository;
import com.example.full_Stack_BP.repository.ClientRepository;
import com.example.full_Stack_BP.repository.TransactionRepository;
import com.example.full_Stack_BP.services.ReportService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ReportServiceImpl implements ReportService {

    private final ClientRepository clientRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    @Override
    public ReportResponseDTO generateReport(Long clientId, LocalDate startDate, LocalDate endDate) {
        log.info("Generating report for client id: {} from {} to {}", clientId, startDate, endDate);

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> {
                    log.error("Client not found with id: {}", clientId);
                    return new ResourceNotFoundException("Client not found with id: " + clientId);
                });

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        List<Account> accounts = accountRepository.findActiveAccountsByClient(client);
        log.debug("Found {} active accounts for client {}", accounts.size(), clientId);

        List<Transaction> transactions = transactionRepository.findByClientAndDateRange(
                clientId, startDateTime, endDateTime);

        BigDecimal totalDeposits = calculateTotalByType(transactions, TransactionType.DEPOSIT);
        BigDecimal totalWithdrawals = calculateTotalByType(transactions, TransactionType.WITHDRAWAL);
        BigDecimal totalTransfers = calculateTotalByType(transactions, TransactionType.TRANSFER);

        log.info("Report generated successfully for client {}. Deposits: {}, Withdrawals: {}, Transfers: {}, Transactions: {}",
                clientId, totalDeposits, totalWithdrawals, totalTransfers, transactions.size());

        return ReportResponseDTO.builder()
                .clientName(client.getName())
                .clientIdentification(client.getIdentification())
                .startDate(startDate)
                .endDate(endDate)
                .accounts(accounts.stream()
                        .map(account -> ReportResponseDTO.AccountSummary.builder()
                                .accountNumber(account.getAccountNumber())
                                .accountType(String.valueOf(account.getAccountType()))
                                .initialBalance(account.getInitialBalance())
                                .currentBalance(account.getCurrentBalance())
                                .status(account.getStatus())
                                .build())
                        .collect(Collectors.toList()))
                .totalDeposits(totalDeposits)
                .totalWithdrawals(totalWithdrawals)
                .totalTransfers(totalTransfers)
                .transactions(transactions.stream()
                        .map(this::mapToTransactionDetail)
                        .collect(Collectors.toList()))
                .build();
    }

    @Override
    public byte[] generatePdfReport(Long clientId, LocalDate startDate, LocalDate endDate) {
        log.info("Generating PDF report for client id: {} from {} to {}",
                clientId, startDate, endDate);

        ReportResponseDTO report = generateReport(clientId, startDate, endDate);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, baos);

            document.open();
            //Titulo
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Paragraph title = new Paragraph("REPORTE DE CUENTAS Y TRANSACCIONES", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Informacion
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            Font normalFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);

            document.add(new Paragraph("Informacion del cliente", headerFont));
            document.add(new Paragraph("Nombre: " + report.getClientName(), normalFont));
            document.add(new Paragraph("Identificacion: " + report.getClientIdentification(), normalFont));
            document.add(new Paragraph("Periodo: " + report.getStartDate().format(DATE_FORMATTER) +
                    " to " + report.getEndDate().format(DATE_FORMATTER), normalFont));
            document.add(Chunk.NEWLINE);

            // Detalle
            document.add(new Paragraph("Resumen:", headerFont));
            PdfPTable accountTable = new PdfPTable(5);
            accountTable.setWidthPercentage(100);
            accountTable.setSpacingBefore(10);
            accountTable.setSpacingAfter(10);

            accountTable.addCell(createHeaderCell("Numero de Cuenta"));
            accountTable.addCell(createHeaderCell("Tipo"));
            accountTable.addCell(createHeaderCell("Balance Inicial"));
            accountTable.addCell(createHeaderCell("Balance Actual"));
            accountTable.addCell(createHeaderCell("Estado"));

            for (ReportResponseDTO.AccountSummary account : report.getAccounts()) {
                accountTable.addCell(createCell(account.getAccountNumber()));
                accountTable.addCell(createCell(account.getAccountType()));
                accountTable.addCell(createCell(account.getInitialBalance().toString()));
                accountTable.addCell(createCell(account.getCurrentBalance().toString()));
                accountTable.addCell(createCell(account.getStatus() ? "Activo" : "Inactivo"));
            }
            document.add(accountTable);

            document.add(new Paragraph("Resumen de la transacción:", headerFont));
            document.add(new Paragraph("Total de depósitos: $" + report.getTotalDeposits(), normalFont));
            document.add(new Paragraph("Total de retiros: $" + report.getTotalWithdrawals(), normalFont));
            document.add(new Paragraph("Total de transferencias: $" + report.getTotalTransfers(), normalFont));
            document.add(Chunk.NEWLINE);

            document.add(new Paragraph("Detalles de la transacción:", headerFont));
            PdfPTable transactionTable = new PdfPTable(6);
            transactionTable.setWidthPercentage(100);
            transactionTable.setSpacingBefore(10);

            transactionTable.addCell(createHeaderCell("Fecha"));
            transactionTable.addCell(createHeaderCell("Cuenta"));
            transactionTable.addCell(createHeaderCell("Tipo"));
            transactionTable.addCell(createHeaderCell("Monto"));
            transactionTable.addCell(createHeaderCell("Balance"));
            transactionTable.addCell(createHeaderCell("Descripción"));

            for (ReportResponseDTO.TransactionDetail transaction : report.getTransactions()) {
                transactionTable.addCell(createCell(transaction.getDate().format(DATE_TIME_FORMATTER)));
                transactionTable.addCell(createCell(transaction.getAccountNumber()));
                transactionTable.addCell(createCell(transaction.getTransactionType().toString()));

                PdfPCell amountCell = createCell("$" + transaction.getAmount());
                if (transaction.getTransactionType() == TransactionType.DEPOSIT) {
                    amountCell.setBackgroundColor(BaseColor.GREEN);
                } else if (transaction.getTransactionType() == TransactionType.WITHDRAWAL) {
                    amountCell.setBackgroundColor(BaseColor.RED);
                } else {
                    amountCell.setBackgroundColor(BaseColor.YELLOW);
                }
                transactionTable.addCell(amountCell);

                transactionTable.addCell(createCell("$" + transaction.getBalance()));
                transactionTable.addCell(createCell(transaction.getDescription()));
            }
            document.add(transactionTable);

            document.add(Chunk.NEWLINE);
            Paragraph footer = new Paragraph(
                    "Generado en: " + LocalDateTime.now().format(DATE_TIME_FORMATTER),
                    new Font(Font.FontFamily.HELVETICA, 8, Font.ITALIC)
            );
            footer.setAlignment(Element.ALIGN_RIGHT);
            document.add(footer);

            document.close();

            log.info("PDF report generated successfully for client {}", clientId);
            return baos.toByteArray();

        } catch (DocumentException e) {
            log.error("Error generating PDF report for client {}: {}", clientId, e.getMessage());
            throw new CustomException(EnumError.REPORT_GENERATION_ERROR);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String generateBase64Report(Long clientId, LocalDate startDate, LocalDate endDate) {
        log.info("Generating Base64 report for client id: {} from {} to {}",
                clientId, startDate, endDate);

        byte[] pdfBytes = generatePdfReport(clientId, startDate, endDate);

        if (pdfBytes.length == 0) {
            log.warn("Empty PDF generated, returning empty Base64 string");
            return "";
        }

        String base64Pdf = Base64.getEncoder().encodeToString(pdfBytes);
        log.info("Base64 report generated successfully for client {}", clientId);
        return base64Pdf;
    }

    private BigDecimal calculateTotalByType(List<Transaction> transactions, TransactionType type) {
        return transactions.stream()
                .filter(t -> t.getTransactionType() == type)
                .map(Transaction::getAmount)
                .map(amount -> type == TransactionType.WITHDRAWAL ? amount.abs() : amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private ReportResponseDTO.TransactionDetail mapToTransactionDetail(Transaction transaction) {
        String destinationAccount = null;
        if (transaction.getTransactionType() == TransactionType.TRANSFER &&
                transaction.getDescription().contains("Transfer")) {
            String desc = transaction.getDescription();
            if (desc.contains("Transfer to account: ")) {
                String[] parts = desc.split("Transfer to account: ");
                if (parts.length > 1) {
                    destinationAccount = parts[1].split(" - ")[0];
                }
            }
        }

        return ReportResponseDTO.TransactionDetail.builder()
                .date(transaction.getDate())
                .accountNumber(transaction.getAccount().getAccountNumber())
                .transactionType(transaction.getTransactionType())
                .amount(transaction.getAmount())
                .balance(transaction.getBalance())
                .description(transaction.getDescription())
                .destinationAccountNumber(destinationAccount)
                .build();
    }

    private PdfPCell createHeaderCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text));
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(5);
        return cell;
    }

    private PdfPCell createCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text));
        cell.setPadding(5);
        return cell;
    }
}
