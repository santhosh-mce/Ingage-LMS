package com.lms.Backend.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Ensures PostgreSQL tables allow null course_id for Career Path purchases and career-level certificates.
 * Hibernate's ddl-auto: update does not drop existing NOT NULL constraints.
 */
@Component
@Order(1)
public class DatabaseSchemaMigrationInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DatabaseSchemaMigrationInitializer.class);

    private final JdbcTemplate jdbcTemplate;

    public DatabaseSchemaMigrationInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {
        dropNotNullSafely("orders", "course_id");
        dropNotNullSafely("payments", "course_id");
        dropNotNullSafely("certificates", "course_id");
    }

    private void dropNotNullSafely(String tableName, String columnName) {
        try {
            jdbcTemplate.execute("ALTER TABLE " + tableName + " ALTER COLUMN " + columnName + " DROP NOT NULL;");
            log.info("[DatabaseMigration] Successfully dropped NOT NULL constraint on {}.{}", tableName, columnName);
        } catch (Exception e) {
            log.warn("[DatabaseMigration] Notice: {}.{} NOT NULL alter skipped: {}", tableName, columnName, e.getMessage());
        }
    }
}
