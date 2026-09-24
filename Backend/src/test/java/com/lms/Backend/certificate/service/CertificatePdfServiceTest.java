package com.lms.Backend.certificate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class CertificatePdfServiceTest {

    private CertificatePdfService pdfService;

    @BeforeEach
    void setUp() {
        pdfService = new CertificatePdfService();
    }

    @Test
    void testDynamicCourseACertificatePdf() {
        String studentName = "Santhosh Kumar D";
        String courseName = "Full Stack Development";
        String certificateNumber = "ING-2026-7842";
        LocalDate completionDate = LocalDate.of(2026, 9, 18);
        String instructorName = "John Smith";

        byte[] pdfBytes = pdfService.generateCertificate(
                studentName,
                courseName,
                certificateNumber,
                completionDate,
                instructorName
        );

        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 1000);
        // Standard PDF magic header check: %PDF-
        assertEquals('%', (char) pdfBytes[0]);
        assertEquals('P', (char) pdfBytes[1]);
        assertEquals('D', (char) pdfBytes[2]);
        assertEquals('F', (char) pdfBytes[3]);
        assertEquals('-', (char) pdfBytes[4]);
    }

    @Test
    void testDynamicCourseBCertificatePdf() {
        String studentName = "Santhosh Kumar D";
        String courseName = "Data Analyst";
        String certificateNumber = "ING-2026-7843";
        LocalDate completionDate = LocalDate.of(2026, 9, 19);
        String instructorName = "Priya Kumar";

        byte[] pdfBytes = pdfService.generateCertificate(
                studentName,
                courseName,
                certificateNumber,
                completionDate,
                instructorName
        );

        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 1000);
        assertEquals('%', (char) pdfBytes[0]);
    }

    @Test
    void testMultipleCoursesProduceDistinctPdfContent() {
        byte[] pdfA = pdfService.generateCertificate(
                "Santhosh Kumar D",
                "Full Stack Development",
                "ING-2026-7842",
                LocalDate.of(2026, 9, 18),
                "John Smith"
        );

        byte[] pdfB = pdfService.generateCertificate(
                "Santhosh Kumar D",
                "Data Analyst",
                "ING-2026-7843",
                LocalDate.of(2026, 9, 19),
                "Priya Kumar"
        );

        assertNotNull(pdfA);
        assertNotNull(pdfB);
        assertNotEquals(pdfA.length, pdfB.length);
    }

    @Test
    void testSanitizeFilename() {
        assertEquals("Full-Stack-Development", CertificatePdfService.sanitizeFilename("Full Stack Development"));
        assertEquals("Data-Analyst", CertificatePdfService.sanitizeFilename("Data Analyst"));
        assertEquals("Python-3.12-Advanced", CertificatePdfService.sanitizeFilename("Python 3.12 & Advanced!"));
        assertEquals("Course", CertificatePdfService.sanitizeFilename(null));
        assertEquals("Course", CertificatePdfService.sanitizeFilename("   "));
    }

    @Test
    void testGetCourseSkills() {
        String[] dataSkills = CertificatePdfService.getCourseSkills("Data Analyst Masterclass");
        assertTrue(dataSkills[0].contains("Python"));
        assertTrue(dataSkills[1].contains("SQL"));

        String[] webSkills = CertificatePdfService.getCourseSkills("Full Stack Web Development");
        assertTrue(webSkills[0].contains("React"));

        String[] pySkills = CertificatePdfService.getCourseSkills("Python Developer");
        assertTrue(pySkills[0].contains("Python"));
    }
}
