package com.lms.Backend.certificate.service;

import com.lms.Backend.certificate.entity.Certificate;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
public class CertificatePdfService {

    private static final Logger log = LoggerFactory.getLogger(CertificatePdfService.class);
    private final Path storageLocation = Paths.get("uploads", "certificates").toAbsolutePath().normalize();

    public CertificatePdfService() {
        try {
            Files.createDirectories(storageLocation);
        } catch (IOException e) {
            log.error("Could not initialize certificate storage directory: {}", e.getMessage());
        }
    }

    public static String sanitizeFilename(String name) {
        if (name == null || name.isBlank()) return "Course";
        String clean = name.replaceAll("[^a-zA-Z0-9.-]", "-").replaceAll("-+", "-").replaceAll("^-|-$", "");
        return clean.isBlank() ? "Course" : clean;
    }

    public static String[] getCourseSkills(String courseName) {
        if (courseName == null) {
            return new String[]{"Core Theory", "Practical Projects", "Best Practices", "Applied Industry Skills"};
        }
        String lower = courseName.toLowerCase();
        if (lower.contains("data") || lower.contains("analyst") || lower.contains("analytics")) {
            return new String[]{"Python (Analysis)", "Pandas & SQL (Querying)", "Tableau & BI (Visuals)", "Statistics (Modeling)"};
        } else if (lower.contains("python")) {
            return new String[]{"Python (Core)", "FastAPI / Django", "PostgreSQL (Database)", "Data Structures (CS)"};
        } else if (lower.contains("full stack") || lower.contains("web") || lower.contains("frontend") || lower.contains("react")) {
            return new String[]{"React (Frontend)", "Node.js (Backend)", "PostgreSQL (Database)", "TypeScript (Language)"};
        } else if (lower.contains("cloud") || lower.contains("devops") || lower.contains("aws")) {
            return new String[]{"Docker & CI/CD", "AWS & Cloud Infra", "Kubernetes", "Linux & Networking"};
        } else if (lower.contains("java") || lower.contains("spring")) {
            return new String[]{"Java 21 (Core)", "Spring Boot (REST)", "PostgreSQL / JPA", "Microservices"};
        } else {
            return new String[]{"Domain Fundamentals", "Hands-on Projects", "Industry Standards", "Technical Competence"};
        }
    }

    /**
     * Generate certificate PDF bytes for student and course details.
     */
    public byte[] generateCertificate(
            String studentName,
            String courseName,
            String certificateNumber,
            LocalDate completionDate,
            String instructorName,
            String verificationCode
    ) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4.rotate(), 36, 36, 36, 36);

        try {
            PdfWriter writer = PdfWriter.getInstance(document, outputStream);
            document.open();

            PdfContentByte canvas = writer.getDirectContent();
            float width = document.getPageSize().getWidth();
            float height = document.getPageSize().getHeight();

            // 1. Draw decorative multi-layered borders (Ingage Green & Slate)
            Color brandGreen = new Color(22, 163, 74);   // #16a34a
            Color accentLime = new Color(141, 182, 0);   // #8DB600
            Color darkSlate = new Color(15, 23, 42);     // #0f172a
            Color textGray = new Color(71, 85, 105);     // #475569

            // Outer primary border
            canvas.setColorStroke(brandGreen);
            canvas.setLineWidth(4.5f);
            canvas.rectangle(20, 20, width - 40, height - 40);
            canvas.stroke();

            // Secondary inner border
            canvas.setColorStroke(darkSlate);
            canvas.setLineWidth(1.2f);
            canvas.rectangle(28, 28, width - 56, height - 56);
            canvas.stroke();

            // Third fine accent border
            canvas.setColorStroke(accentLime);
            canvas.setLineWidth(0.8f);
            canvas.rectangle(32, 32, width - 64, height - 64);
            canvas.stroke();

            // Corner decorative accents
            canvas.setColorFill(brandGreen);
            canvas.circle(20, 20, 4);
            canvas.circle(width - 20, 20, 4);
            canvas.circle(20, height - 20, 4);
            canvas.circle(width - 20, height - 20, 4);
            canvas.fill();

            // 2. Fonts
            Font brandFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, darkSlate);
            Font taglineFont = FontFactory.getFont(FontFactory.HELVETICA, 11, textGray);
            Font certTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, brandGreen);
            Font subTitleFont = FontFactory.getFont(FontFactory.HELVETICA, 13, textGray);
            Font nameFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 26, darkSlate);
            Font courseFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, brandGreen);
            Font metaFont = FontFactory.getFont(FontFactory.HELVETICA, 11, textGray);
            Font metaBoldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, darkSlate);
            Font idFont = FontFactory.getFont(FontFactory.COURIER_BOLD, 12, darkSlate);
            Font signNameFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, darkSlate);
            Font signRoleFont = FontFactory.getFont(FontFactory.HELVETICA, 10, textGray);

            // 3. Header Table: Ingage Logo & Tagline (Left) | Certificate ID & Date (Right)
            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(92);
            headerTable.setWidths(new float[]{60, 40});

            PdfPCell leftHeader = new PdfPCell();
            leftHeader.setBorder(PdfPCell.NO_BORDER);
            leftHeader.addElement(new Paragraph("ingage  |  Learn • Build • Grow", brandFont));
            leftHeader.addElement(new Paragraph("Official Accredited E-Learning Platform", taglineFont));
            headerTable.addCell(leftHeader);

            PdfPCell rightHeader = new PdfPCell();
            rightHeader.setBorder(PdfPCell.NO_BORDER);
            rightHeader.setHorizontalAlignment(Element.ALIGN_RIGHT);
            Paragraph idPara = new Paragraph("Certificate ID: " + certificateNumber, idFont);
            idPara.setAlignment(Element.ALIGN_RIGHT);
            Paragraph datePara = new Paragraph("Issued on: " + completionDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")), metaFont);
            datePara.setAlignment(Element.ALIGN_RIGHT);
            rightHeader.addElement(idPara);
            rightHeader.addElement(datePara);
            headerTable.addCell(rightHeader);

            document.add(headerTable);

            // 4. Certificate Title
            Paragraph titlePara = new Paragraph("CERTIFICATE OF COMPLETION", certTitleFont);
            titlePara.setAlignment(Element.ALIGN_CENTER);
            titlePara.setSpacingBefore(18);
            document.add(titlePara);

            // 5. Course Title
            Paragraph coursePara = new Paragraph(courseName, courseFont);
            coursePara.setAlignment(Element.ALIGN_CENTER);
            coursePara.setSpacingBefore(6);
            document.add(coursePara);

            // 6. Presented to
            Paragraph certifyPara = new Paragraph("This certifies that", subTitleFont);
            certifyPara.setAlignment(Element.ALIGN_CENTER);
            certifyPara.setSpacingBefore(14);
            document.add(certifyPara);

            // 7. Student Name
            Paragraph studentPara = new Paragraph(studentName, nameFont);
            studentPara.setAlignment(Element.ALIGN_CENTER);
            studentPara.setSpacingBefore(8);
            document.add(studentPara);

            // 8. Description statement
            Paragraph descPara = new Paragraph(
                    "has successfully completed the " + courseName + " course offered by Ingage LMS " +
                    "and demonstrated the required skills and knowledge in modern technologies.",
                    metaFont
            );
            descPara.setAlignment(Element.ALIGN_CENTER);
            descPara.setSpacingBefore(10);
            document.add(descPara);

            // 9. Skills Summary Table
            PdfPTable skillsTable = new PdfPTable(4);
            skillsTable.setWidthPercentage(85);
            skillsTable.setSpacingBefore(14);
            String[] skills = getCourseSkills(courseName);
            for (String skill : skills) {
                PdfPCell skillCell = new PdfPCell(new Paragraph("✓ " + skill, metaBoldFont));
                skillCell.setBorder(PdfPCell.BOX);
                skillCell.setBorderColor(new Color(229, 231, 235));
                skillCell.setBackgroundColor(new Color(240, 253, 244));
                skillCell.setPadding(6);
                skillCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                skillsTable.addCell(skillCell);
            }
            document.add(skillsTable);

            // 10. Footer Signatures & Quote
            PdfPTable footerTable = new PdfPTable(2);
            footerTable.setWidthPercentage(92);
            footerTable.setSpacingBefore(20);

            PdfPCell leftFooter = new PdfPCell();
            leftFooter.setBorder(PdfPCell.NO_BORDER);
            leftFooter.addElement(new Paragraph("Keep building, keep growing.", metaBoldFont));
            leftFooter.addElement(new Paragraph("Your next chapter is just the beginning!", taglineFont));
            footerTable.addCell(leftFooter);

            PdfPCell rightFooter = new PdfPCell();
            rightFooter.setBorder(PdfPCell.NO_BORDER);
            rightFooter.setHorizontalAlignment(Element.ALIGN_RIGHT);

            boolean hasInstructor = instructorName != null && !instructorName.trim().isEmpty();
            String signatureLabel = hasInstructor ? instructorName.trim() : "Academic Board";
            String signatureRole = hasInstructor ? "Course Instructor, Ingage LMS" : "Ingage Certification Authority";

            Paragraph sigLine = new Paragraph("___________________________", metaFont);
            sigLine.setAlignment(Element.ALIGN_RIGHT);
            Paragraph sigName = new Paragraph(signatureLabel, signNameFont);
            sigName.setAlignment(Element.ALIGN_RIGHT);
            Paragraph sigRole = new Paragraph(signatureRole, signRoleFont);
            sigRole.setAlignment(Element.ALIGN_RIGHT);
            rightFooter.addElement(sigLine);
            rightFooter.addElement(sigName);
            rightFooter.addElement(sigRole);
            footerTable.addCell(rightFooter);

            document.add(footerTable);

            // 11. Verification Ledger Footer
            String verifyCode = (verificationCode != null && !verificationCode.isBlank()) ? verificationCode : certificateNumber;
            Paragraph verifyPara = new Paragraph(
                    "Official Verification Ledger: /certificate/verify/" + verifyCode + "  •  100% Tamper-Proof  •  Issuer: InGage Edutech",
                    FontFactory.getFont(FontFactory.HELVETICA, 8, textGray)
            );
            verifyPara.setAlignment(Element.ALIGN_CENTER);
            verifyPara.setSpacingBefore(12);
            document.add(verifyPara);

            document.close();
            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error("Failed to generate certificate PDF: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate certificate PDF", e);
        }
    }

    /**
     * Generate PDF from a Certificate entity and save it to backend storage.
     */
    public byte[] generateAndStoreCertificatePdf(Certificate certificate) {
        String studentName = certificate.getStudentName();
        String courseName = certificate.getCourseName();
        String certificateNumber = certificate.getCertificateNumber();
        String instructorName = certificate.getInstructorName();
        String verificationCode = certificate.getVerificationCode();

        Instant compInstant = certificate.getCompletionDate() != null ? certificate.getCompletionDate() : certificate.getIssuedAt();
        LocalDate completionDate = compInstant.atZone(ZoneId.systemDefault()).toLocalDate();

        byte[] pdfBytes = generateCertificate(
                studentName,
                courseName,
                certificateNumber,
                completionDate,
                instructorName,
                verificationCode
        );

        // Save to backend disk storage
        try {
            Files.createDirectories(storageLocation);
            String sanitizedCourse = sanitizeFilename(courseName);
            String filename = "Certificate-" + sanitizedCourse + "-" + certificateNumber + ".pdf";
            Path filePath = storageLocation.resolve(filename);
            Files.write(filePath, pdfBytes);
            certificate.setPdfPath(filePath.toString());
            log.info("Certificate PDF stored at: {}", filePath);
        } catch (IOException e) {
            log.warn("Could not save certificate PDF to disk: {}", e.getMessage());
        }

        return pdfBytes;
    }

    /**
     * Read the certificate PDF bytes from storage or generate dynamically if not present.
     */
    public byte[] getCertificatePdfBytes(Certificate certificate) {
        if (certificate.getPdfPath() != null && !certificate.getPdfPath().isBlank()) {
            Path filePath = Paths.get(certificate.getPdfPath());
            if (Files.exists(filePath)) {
                try {
                    return Files.readAllBytes(filePath);
                } catch (IOException e) {
                    log.warn("Failed to read stored certificate PDF from disk: {}. Regenerating...", e.getMessage());
                }
            }
        }

        // Fallback: check storage location with sanitized course title or standard filename
        String sanitizedCourse = sanitizeFilename(certificate.getCourse() != null ? certificate.getCourse().getTitle() : null);
        Path namedPath = storageLocation.resolve("Certificate-" + sanitizedCourse + "-" + certificate.getCertificateNumber() + ".pdf");
        if (Files.exists(namedPath)) {
            try {
                return Files.readAllBytes(namedPath);
            } catch (IOException ignored) {}
        }

        Path defaultPath = storageLocation.resolve("Certificate-" + certificate.getCertificateNumber() + ".pdf");
        if (Files.exists(defaultPath)) {
            try {
                return Files.readAllBytes(defaultPath);
            } catch (IOException ignored) {}
        }

        // Regenerate and store
        return generateAndStoreCertificatePdf(certificate);
    }

    /**
     * Overloaded method for dynamic certificate generation with default verificationCode.
     */
    public byte[] generateCertificate(
            String studentName,
            String courseName,
            String certificateNumber,
            LocalDate completionDate,
            String instructorName
    ) {
        return generateCertificate(studentName, courseName, certificateNumber, completionDate, instructorName, certificateNumber);
    }

    // =========================================================================
    // DEVELOPMENT / TESTING ONLY - NEVER USED FOR REAL COURSE COMPLETION
    // =========================================================================
    public byte[] generateSampleCertificatePdf() {
        return generateCertificate(
                "Sample Learner",
                "Sample Certification Course",
                "ING-2026-0000",
                LocalDate.now(),
                "Lead Instructor",
                "ING-2026-0000"
        );
    }
}
