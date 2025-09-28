-- =============================================
-- Add SUPER_ADMIN role and initial super admin user
-- =============================================

-- Update the role check constraint to include SUPER_ADMIN
ALTER TABLE users DROP CONSTRAINT IF EXISTS chk_role;
ALTER TABLE users ADD CONSTRAINT chk_role CHECK (role IN ('STUDENT', 'TEACHER', 'ADMIN', 'SUPER_ADMIN'));

-- Insert initial super admin user (password: SuperAdmin@123)
INSERT INTO users (username, email, password_hash, first_name, last_name, role, enabled, created_at, updated_at) 
VALUES (
    'superadmin', 
    'superadmin@koushaljha.com', 
    '$2a$12$r4.AG.7QdL1w3V5hY8sB6.BcD5EfG7H.I9J0K1L2M3N4O5P6Q7R8S9T0', 
    'System', 
    'Super Administrator', 
    'SUPER_ADMIN', 
    true, 
    CURRENT_TIMESTAMP, 
    CURRENT_TIMESTAMP
);

-- Create admin_creation_log table to track admin creations
CREATE TABLE admin_creation_log (
    id BIGSERIAL PRIMARY KEY,
    admin_username VARCHAR(50) NOT NULL,
    admin_email VARCHAR(100) NOT NULL,
    created_by VARCHAR(50) NOT NULL, -- superadmin username who created this admin
    temporary_password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    password_changed BOOLEAN DEFAULT false,
    password_changed_at TIMESTAMP NULL
);

-- Create index for better performance
CREATE INDEX idx_admin_creation_log_created_by ON admin_creation_log(created_by);
CREATE INDEX idx_admin_creation_log_admin_username ON admin_creation_log(admin_username);