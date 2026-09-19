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
        Optional<Certificate> certOpt = certificateService.getCertificateByVerificationCode(verificationCode);
        if (certOpt.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of(
                "valid", false,
                "message", "Certificate not found or invalid verification code."
            ));
        }

        Certificate cert = certOpt.get();
        // Publicly verifiable data only — no private user credentials
        return ResponseEntity.ok(Map.of(
            "valid", true,
            "status", cert.getStatus().name(),
            "studentName", cert.getUser().getName(),
            "courseName", cert.getCourse().getTitle(),
            "courseCategory", cert.getCourse().getCategory() != null ? cert.getCourse().getCategory() : "Professional Learning",
            "issueDate", cert.getIssuedAt().toString(),
            "certificateNumber", cert.getCertificateNumber(),
            "verificationCode", cert.getVerificationCode()
        ));
    }

    @GetMapping("/verify/{verificationCode}/download")
    public ResponseEntity<byte[]> downloadPublicCertificatePdf(@PathVariable String verificationCode) {
        Optional<Certificate> certOpt = certificateService.getCertificateByVerificationCode(verificationCode);
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
