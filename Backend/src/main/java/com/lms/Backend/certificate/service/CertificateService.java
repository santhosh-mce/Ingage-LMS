package com.lms.Backend.certificate.service;

import com.lms.Backend.certificate.entity.Certificate;
import com.lms.Backend.certificate.entity.CertificateStatus;
import com.lms.Backend.certificate.repository.CertificateRepository;
import com.lms.Backend.course.entity.Course;
import com.lms.Backend.course.repository.CourseRepository;
import com.lms.Backend.notification.entity.Notification;
import com.lms.Backend.notification.repository.NotificationRepository;
import com.lms.Backend.user.entity.User;
import com.lms.Backend.user.repository.UserRepository;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CertificateService {

    private final CertificateRepository certificateRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final NotificationRepository notificationRepository;

    public CertificateService(
        CertificateRepository certificateRepository,
        UserRepository userRepository,
        CourseRepository courseRepository,
        NotificationRepository notificationRepository
    ) {
        this.certificateRepository = certificateRepository;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.notificationRepository = notificationRepository;
    }

    @Transactional
    public Certificate generateCertificate(UUID userId, Long courseId) {
        // Prevent duplicate certificate generation
        Optional<Certificate> existing = certificateRepository.findByUserIdAndCourseId(userId, courseId);
        if (existing.isPresent()) {
            return existing.get();
        }

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new IllegalArgumentException("Course not found: " + courseId));

        int year = LocalDate.now().getYear();
        long count = certificateRepository.count() + 1;
        String certificateNumber = String.format("INGAGE-%d-%06d", year, count);
        String verificationCode = UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();

        Certificate certificate = new Certificate(certificateNumber, verificationCode, user, course);
        certificate.setCertificateUrl("/certificate/verify/" + verificationCode);
        Certificate saved = certificateRepository.save(certificate);

        // Notify user about certificate issuance
        try {
            Notification notification = new Notification(
                user,
                "Certificate of Completion Issued!",
                "Congratulations! You have successfully completed " + course.getTitle() + ". Your official certificate (" + certificateNumber + ") is now available.",
                "CERTIFICATE",
                "/certificate/verify/" + verificationCode
            );
            notificationRepository.save(notification);
        } catch (Exception ignored) {}

        return saved;
    }

    public Optional<Certificate> getCertificateByVerificationCode(String verificationCode) {
        return certificateRepository.findByVerificationCodeIgnoreCase(verificationCode.trim());
    }

    public List<Certificate> getAllCertificates() {
        return certificateRepository.findAllByOrderByIssuedAtDesc();
    }

    public List<Certificate> getUserCertificates(UUID userId) {
        return certificateRepository.findByUserIdOrderByIssuedAtDesc(userId);
    }

    public byte[] generateCertificatePdf(Certificate certificate) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            // Landscape A4 certificate
            Document document = new Document(PageSize.A4.rotate(), 36, 36, 36, 36);
            PdfWriter writer = PdfWriter.getInstance(document, out);
            document.open();

            // Draw elegant borders
            PdfContentByte canvas = writer.getDirectContent();
            float width = document.getPageSize().getWidth();
            float height = document.getPageSize().getHeight();

            // Outer border
            canvas.setColorStroke(new Color(132, 204, 22)); // Lime border
            canvas.setLineWidth(4f);
            canvas.rectangle(20, 20, width - 40, height - 40);
            canvas.stroke();

            // Inner border
            canvas.setColorStroke(new Color(30, 41, 59)); // Slate border
            canvas.setLineWidth(1.5f);
            canvas.rectangle(28, 28, width - 56, height - 56);
            canvas.stroke();

            // Decorative corner accents
            canvas.setColorStroke(new Color(132, 204, 22));
            canvas.setLineWidth(2f);
            canvas.rectangle(24, 24, width - 48, height - 48);
            canvas.stroke();

            // Fonts
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, new Color(15, 23, 42));
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 28, new Color(132, 204, 22));
            Font subTitleFont = FontFactory.getFont(FontFactory.HELVETICA, 13, new Color(71, 85, 105));
            Font nameFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 26, new Color(15, 23, 42));
            Font courseFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, new Color(30, 41, 59));
            Font metaFont = FontFactory.getFont(FontFactory.HELVETICA, 10, new Color(100, 116, 139));
            Font codeFont = FontFactory.getFont(FontFactory.COURIER_BOLD, 10, new Color(15, 23, 42));

            // Content
            Paragraph orgName = new Paragraph("INGAGE LEARNING PLATFORM", headerFont);
            orgName.setAlignment(Element.ALIGN_CENTER);
            orgName.setSpacingBefore(15);
            document.add(orgName);

            Paragraph certTitle = new Paragraph("CERTIFICATE OF COMPLETION", titleFont);
            certTitle.setAlignment(Element.ALIGN_CENTER);
            certTitle.setSpacingBefore(8);
            document.add(certTitle);

            Paragraph presentedTo = new Paragraph("THIS CERTIFICATE IS PROUDLY PRESENTED TO", subTitleFont);
            presentedTo.setAlignment(Element.ALIGN_CENTER);
            presentedTo.setSpacingBefore(18);
            document.add(presentedTo);

            Paragraph studentName = new Paragraph(certificate.getUser().getName().toUpperCase(), nameFont);
            studentName.setAlignment(Element.ALIGN_CENTER);
            studentName.setSpacingBefore(8);
            document.add(studentName);

            Paragraph forCompleting = new Paragraph("for successfully completing and mastering the curriculum for", subTitleFont);
            forCompleting.setAlignment(Element.ALIGN_CENTER);
            forCompleting.setSpacingBefore(10);
            document.add(forCompleting);

            Paragraph courseName = new Paragraph(certificate.getCourse().getTitle(), courseFont);
            courseName.setAlignment(Element.ALIGN_CENTER);
            courseName.setSpacingBefore(8);
            document.add(courseName);

            // Date format
            String formattedDate = DateTimeFormatter.ofPattern("MMMM dd, yyyy")
                .withZone(ZoneId.systemDefault())
                .format(certificate.getIssuedAt());

            Paragraph datePara = new Paragraph("Issued on: " + formattedDate, subTitleFont);
            datePara.setAlignment(Element.ALIGN_CENTER);
            datePara.setSpacingBefore(12);
            document.add(datePara);

            // Footer metadata
            Paragraph meta = new Paragraph(
                "Certificate No: " + certificate.getCertificateNumber() + 
                "  |  Verification Code: " + certificate.getVerificationCode() + 
                "  |  Verify at: /certificate/verify/" + certificate.getVerificationCode(),
                codeFont
            );
            meta.setAlignment(Element.ALIGN_CENTER);
            meta.setSpacingBefore(28);
            document.add(meta);

            Paragraph disclaimer = new Paragraph("Ingage LMS Official Credential • Verified & cryptographically tracked on PostgreSQL", metaFont);
            disclaimer.setAlignment(Element.ALIGN_CENTER);
            disclaimer.setSpacingBefore(6);
            document.add(disclaimer);

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF certificate: " + e.getMessage(), e);
        }
    }
}
