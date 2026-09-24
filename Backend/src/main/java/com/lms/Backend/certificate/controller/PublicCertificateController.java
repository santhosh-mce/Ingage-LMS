package com.lms.Backend.certificate.controller;

import com.lms.Backend.certificate.entity.Certificate;
import com.lms.Backend.certificate.service.CertificateService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/certificates")
public class PublicCertificateController {

    private final CertificateService certificateService;

    public PublicCertificateController(CertificateService certificateService) {
        this.certificateService = certificateService;
    }

    @GetMapping("/verify/{verificationCode}")
    public ResponseEntity<?> verifyCertificate(@PathVariable String verificationCode) {
        Optional<Certificate> certOpt = certificateService.findByCodeOrNumber(verificationCode);
        if (certOpt.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of(
                "valid", false,
                "message", "Certificate not found or invalid certificate ID/verification code."
            ));
        }

        Certificate cert = certOpt.get();
        java.time.Instant compDate = cert.getCompletionDate() != null ? cert.getCompletionDate() : cert.getIssuedAt();
        String formattedDate = java.time.format.DateTimeFormatter.ofPattern("dd MMMM yyyy")
            .withZone(java.time.ZoneId.systemDefault())
            .format(compDate != null ? compDate : java.time.Instant.now());

        // Publicly verifiable data only — no private user credentials
        Map<String, Object> resp = new java.util.LinkedHashMap<>();
        resp.put("valid", true);
        resp.put("status", cert.getStatus().name());
        resp.put("studentName", cert.getUser().getName());
        resp.put("courseName", cert.getCourse().getTitle());
        resp.put("courseCategory", cert.getCourse().getCategory() != null ? cert.getCourse().getCategory() : "Professional Learning");
        resp.put("courseDuration", cert.getCourse().getDuration() != null ? cert.getCourse().getDuration() : "");
        resp.put("instructor", cert.getCourse().getInstructor() != null ? cert.getCourse().getInstructor() : "Lead Instructor");
        resp.put("completionDate", compDate != null ? compDate.toString() : "");
        resp.put("formattedDate", formattedDate);
        resp.put("issueDate", cert.getIssuedAt().toString());
        resp.put("certificateNumber", cert.getCertificateNumber());
        resp.put("verificationCode", cert.getVerificationCode());
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/verify/{verificationCode}/download")
    public ResponseEntity<byte[]> downloadPublicCertificatePdf(@PathVariable String verificationCode) {
        Optional<Certificate> certOpt = certificateService.findByCodeOrNumber(verificationCode);
        if (certOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Certificate cert = certOpt.get();
        byte[] pdfBytes = certificateService.generateCertificatePdf(cert);

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"certificate-" + cert.getCertificateNumber() + ".pdf\"")
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdfBytes);
    }
}
