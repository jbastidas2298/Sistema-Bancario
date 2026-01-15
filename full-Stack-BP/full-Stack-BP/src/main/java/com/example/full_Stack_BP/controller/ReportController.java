package com.example.full_Stack_BP.controller;

import com.example.full_Stack_BP.dto.response.ReportResponseDTO;
import com.example.full_Stack_BP.services.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Reports", description = "Report generation endpoints")
public class ReportController {

    private final ReportService reportService;

    @GetMapping
    @Operation(summary = "Generate bank statement report")
    public ResponseEntity<ReportResponseDTO> generateReport(
            @RequestParam Long clientId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        log.info("Received request to generate report for client {} from {} to {}",
                clientId, startDate, endDate);

        ReportResponseDTO report = reportService.generateReport(clientId, startDate, endDate);
        log.info("Report generated successfully for client {}", clientId);

        return ResponseEntity.ok(report);
    }

    @GetMapping("/pdf")
    @Operation(summary = "Generate PDF report")
    public ResponseEntity<byte[]> generatePdfReport(
            @RequestParam Long clientId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        log.info("Received request to generate PDF report for client {} from {} to {}",
                clientId, startDate, endDate);

        byte[] pdfBytes = reportService.generatePdfReport(clientId, startDate, endDate);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment",
                String.format("bank-statement-%s-%s-to-%s.pdf",
                        clientId, startDate, endDate));
        headers.setContentLength(pdfBytes.length);

        log.info("PDF report generated successfully for client {}", clientId);
        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    @GetMapping("/base64")
    @Operation(summary = "Generate Base64 encoded PDF report")
    public ResponseEntity<String> generateBase64Report(
            @RequestParam Long clientId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        log.info("Received request to generate Base64 report for client {} from {} to {}",
                clientId, startDate, endDate);

        String base64Report = reportService.generateBase64Report(clientId, startDate, endDate);
        log.info("Base64 report generated successfully for client {}", clientId);

        return ResponseEntity.ok(base64Report);
    }
}