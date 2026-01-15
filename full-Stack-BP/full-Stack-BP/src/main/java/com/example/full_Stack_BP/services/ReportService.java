package com.example.full_Stack_BP.services;

import com.example.full_Stack_BP.dto.response.ReportResponseDTO;

import java.time.LocalDate;

public interface ReportService {
    ReportResponseDTO generateReport(Long clientId, LocalDate startDate, LocalDate endDate);
    byte[] generatePdfReport(Long clientId, LocalDate startDate, LocalDate endDate);
    String generateBase64Report(Long clientId, LocalDate startDate, LocalDate endDate);
}
