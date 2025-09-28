package com.koushaljhait.repository.common;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.koushaljhait.model.common.PasswordResetOtp;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PasswordResetOtpRepository extends JpaRepository<PasswordResetOtp, Long> {
    
    Optional<PasswordResetOtp> findByEmail(String email);
    
    @Query("SELECT o FROM PasswordResetOtp o WHERE o.email = :email AND o.used = false AND o.expiresAt > :now")
    Optional<PasswordResetOtp> findValidOtpByEmail(@Param("email") String email, @Param("now") LocalDateTime now);
    
    @Modifying
    @Query("DELETE FROM PasswordResetOtp o WHERE o.expiresAt < :now OR o.used = true")
    void deleteExpiredOtps(@Param("now") LocalDateTime now);
    
    // REMOVED the markOtpAsUsed method - we'll update the entity directly instead
}