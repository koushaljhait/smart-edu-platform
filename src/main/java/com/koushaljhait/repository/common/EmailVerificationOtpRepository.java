package com.koushaljhait.repository.common;

import com.koushaljhait.model.common.EmailVerificationOtp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface EmailVerificationOtpRepository extends JpaRepository<EmailVerificationOtp, Long> {
    
    Optional<EmailVerificationOtp> findByEmail(String email);
    
    @Query("SELECT o FROM EmailVerificationOtp o WHERE o.email = :email AND o.verified = false AND o.expiresAt > :now")
    Optional<EmailVerificationOtp> findValidOtpByEmail(@Param("email") String email, @Param("now") LocalDateTime now);
    
    Optional<EmailVerificationOtp> findByEmailAndVerified(String email, boolean verified);
}