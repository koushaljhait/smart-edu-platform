CREATE TABLE email_verification_otp (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,
    otp_code VARCHAR(10) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL,
    verified BOOLEAN DEFAULT FALSE
);

CREATE INDEX idx_email_verification_otp_email ON email_verification_otp(email);
CREATE INDEX idx_email_verification_otp_expires ON email_verification_otp(expires_at);