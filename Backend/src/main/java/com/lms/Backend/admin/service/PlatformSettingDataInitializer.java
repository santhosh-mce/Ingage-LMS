package com.lms.Backend.admin.service;

import com.lms.Backend.admin.entity.PlatformSetting;
import com.lms.Backend.admin.repository.PlatformSettingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
@Order(10)
public class PlatformSettingDataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(PlatformSettingDataInitializer.class);

    private final PlatformSettingRepository settingRepository;

    public PlatformSettingDataInitializer(PlatformSettingRepository settingRepository) {
        this.settingRepository = settingRepository;
    }

    @Override
    public void run(String... args) {
        if (settingRepository.count() == 0) {
            log.info("[PlatformSettingsInitializer] Initializing default LMS platform settings in PostgreSQL...");

            Map<String, String[]> defaults = new LinkedHashMap<>();

            // General Settings [key -> [value, category]]
            defaults.put("platform_name", new String[]{"Ingage", "GENERAL"});
            defaults.put("platform_description", new String[]{"Next-generation interactive learning, career roadmaps, and internship opportunities platform.", "GENERAL"});
            defaults.put("support_email", new String[]{"support@ingage.com", "GENERAL"});
            defaults.put("support_phone", new String[]{"+1 (555) 000-1234", "GENERAL"});
            defaults.put("website_url", new String[]{"https://ingage-lms.vercel.app", "GENERAL"});
            defaults.put("timezone", new String[]{"Asia/Kolkata (IST)", "GENERAL"});
            defaults.put("default_language", new String[]{"English", "GENERAL"});

            // Notification Settings
            defaults.put("notify_new_user_registration", new String[]{"true", "NOTIFICATIONS"});
            defaults.put("notify_course_enrollment", new String[]{"true", "NOTIFICATIONS"});
            defaults.put("notify_payment_success", new String[]{"true", "NOTIFICATIONS"});
            defaults.put("notify_certificate_generated", new String[]{"true", "NOTIFICATIONS"});
            defaults.put("notify_password_reset", new String[]{"true", "NOTIFICATIONS"});
            defaults.put("notify_email_verification", new String[]{"true", "NOTIFICATIONS"});

            // Platform Settings
            defaults.put("allow_user_registration", new String[]{"true", "PLATFORM"});
            defaults.put("require_email_verification", new String[]{"false", "PLATFORM"});
            defaults.put("allow_course_enrollment", new String[]{"true", "PLATFORM"});
            defaults.put("allow_opportunity_applications", new String[]{"true", "PLATFORM"});
            defaults.put("maintenance_mode", new String[]{"false", "PLATFORM"});

            // Email Settings
            defaults.put("email_provider", new String[]{"In-App / System Logger", "EMAIL"});
            defaults.put("email_sender_name", new String[]{"Ingage LMS Support", "EMAIL"});
            defaults.put("email_sender_email", new String[]{"no-reply@ingage.com", "EMAIL"});

            for (Map.Entry<String, String[]> entry : defaults.entrySet()) {
                PlatformSetting setting = new PlatformSetting(
                    entry.getKey(),
                    entry.getValue()[0],
                    entry.getValue()[1],
                    "system"
                );
                settingRepository.save(setting);
            }

            log.info("[PlatformSettingsInitializer] Seeded {} default platform settings successfully.", defaults.size());
        }
    }
}
