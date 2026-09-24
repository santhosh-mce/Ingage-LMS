package com.lms.Backend.user.repository;

import com.lms.Backend.user.entity.AuthProvider;
import com.lms.Backend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    boolean existsByEmailIgnoreCase(String email);

    Optional<User> findByEmailIgnoreCase(String email);

    Optional<User> findByProviderAndProviderId(AuthProvider provider, String providerId);

    long countByRole(com.lms.Backend.user.entity.UserRole role);

    long countByActiveTrue();

    long countByActiveFalse();

    long countByEmailVerifiedTrue();

    long countByEmailVerifiedFalse();

    long countByCreatedAtAfter(java.time.Instant date);

    long countByCreatedAtBetween(java.time.Instant start, java.time.Instant end);

    java.util.List<User> findTop10ByOrderByCreatedAtDesc();

    java.util.List<User> findAllByOrderByCreatedAtDesc();

    @org.springframework.data.jpa.repository.Query("SELECT u FROM User u WHERE " +
           "LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "ORDER BY u.createdAt DESC")
    java.util.List<User> searchUsers(@org.springframework.data.repository.query.Param("search") String search);
}