package com.gymmanagement.gym_app.controller;

import com.gymmanagement.gym_app.model.ReportModel;
import com.gymmanagement.gym_app.service.ReportService;
import com.gymmanagement.gym_app.utils.PdfReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final PdfReportService pdfReportService;
    @GetMapping("/retention")
    public ResponseEntity<ReportModel> getRetentionReport() {
        ReportModel report = reportService.getRetentionReport();
        return ResponseEntity.ok(report);
    }

    @GetMapping("/retention/pdf")
    public ResponseEntity<byte[]> getRetentionReportPdf() {
        ReportModel report = reportService.getRetentionReport();
        ByteArrayInputStream pdfStream = pdfReportService.generateRetentionReportPdf(report);
        byte[] pdfBytes = pdfStream.readAllBytes();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=retention_report.pdf");

        return ResponseEntity.ok().headers(headers).body(pdfBytes);
    }

}
