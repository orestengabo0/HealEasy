-- Add initial admin user with BCrypt-encoded password
INSERT INTO users (user_name, email, password, phone_number, role, created_at, updated_at)
VALUES ('admin', 'admin@healeasy.com',
        '$2a$12$.prwg6emPb5BT.V37Yul0uIgN6sgdxRia8/2APMy9AGnrKSzamz1u', -- password: AdminPassCode1@123
        '0738049975', 'ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (email) DO NOTHING; -- Skip if admin user already exists