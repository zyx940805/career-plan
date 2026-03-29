package com.career.plan.controller;

import com.career.plan.service.ProfileServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 简历导出接口
 */
@RestController
@RequestMapping("/api/export")
public class PdfController {

    @Autowired
    private ProfileServiceImpl.ResumePdfService resumePdfService;

    @GetMapping("/resume/{userId}")
    public ResponseEntity<byte[]> downloadResume(@PathVariable Long userId) {
        try {
            byte[] pdfBytes = resumePdfService.generateResumePdf(userId);

            String fileName = "resume_" + userId + ".pdf";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}