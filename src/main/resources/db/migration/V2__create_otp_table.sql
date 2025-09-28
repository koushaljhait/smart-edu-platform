-- =============================================
-- Password Reset OTP Table
-- Version: 1.0.0.0
-- =============================================

CREATE TABLE password_reset_otp (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    otp_code VARCHAR(10) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL,
    used BOOLEAN DEFAULT FALSE,
    CONSTRAINT uk_otp_email UNIQUE (email)
);

CREATE INDEX idx_otp_email ON password_reset_otp(email);
CREATE INDEX idx_otp_expires ON password_reset_otp(expires_at);

-- Comment for documentation
COMMENT ON TABLE password_reset_otp IS 'Stores OTP codes for password reset functionality';