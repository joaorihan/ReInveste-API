ALTER TABLE usuarios ADD COLUMN senha VARCHAR(255) NOT NULL DEFAULT '$2a$10$default.encrypted.password.placeholder';

