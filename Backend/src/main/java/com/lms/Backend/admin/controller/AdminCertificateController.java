package com.lms.Backend.admin.controller;

import com.lms.Backend.admin.service.AdminActivityLogService;
import com.lms.Backend.certificate.entity.Certificate;
import com.lms.Backend.certificate.repository.CertificateRepository;
import com.lms.Backend.certificate.service.CertificateService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/admin/certificates")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCertificateController {

    private final CertificateService certificateService;
    private final CertificateRepository certificateRepository;
    private final AdminActivityLogService logService;

    public AdminCertificateController(
        CertificateService certificateService,
        CertificateRepository certificateRepository,
        AdminActivityLogService logService
    ) {
        this.certificateService = certificateService;
        this.certificateRepository = certificateRepository;
        this.logService = logService;
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllCertificates() {
        List<Certificate> certs = certificateService.getAllCertificates();
        List<Map<String, Object>> result = new ArrayList<>();

        for (Certificate c : certs) {
            Map<String, Object> dto = new LinkedHashMap<>();
            dto.put("id", c.getId());
            dto.put("certificateNumber", c.getCertificateNumber());
            dto.put("verificationCode", c.getVerificationCode());
            dto.put("userId", c.getUser().getId());
            dto.put("userName", c.getUser().getName());
            dto.put("userEmail", c.getUser().getEmail());
            dto.put("courseId", c.getCourse().getId());
            dto.put("courseTitle", c.getCourse().getTitle());
            dto.put("issueDate", c.getIssuedAt().toString());
            dto.put("status", c.getStatus().name());
            dto.put("verificationUrl", "/certificate/verify/" + c.getVerificationCode());
            dto.put("downloadUrl", "/api/certificates/verify/" + c.getVerificationCode() + "/download");
            result.add(dto);
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadCertificatePdf(@PathVariable Long id) {
        Certificate cert = certificateRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Certificate not found: " + id));

        byte[] pdfBytes = certificateService.generateCertificatePdf(cert);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"certificate-" + cert.getCertificateNumber() + ".pdf\"")
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdfBytes);
    }

    @PostMapping("/issue")
    public ResponseEntity<Certificate> issueManualCertificate(
        @RequestBody Map<String, Object> payload,
        Principal principal,
        HttpServletRequest request
    ) {
        UUID userId = UUID.fromString((String) payload.get("userId"));
        Long courseId = Long.parseLong(payload.get("courseId").toString());

        Certificate cert = certificateService.generateCertificate(userId, courseId);
        logService.log(null, principal != null ? principal.getName() : "admin", "ISSUE_CERTIFICATE", "Certificate", cert.getId().toString(), "Manual issue for user " + userId, request);
        return ResponseEntity.ok(cert);
    }
}
