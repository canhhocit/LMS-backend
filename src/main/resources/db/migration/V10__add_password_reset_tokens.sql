-- V10: Add password_reset_tokens table for forgot password feature
CREATE TABLE password_reset_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token VARCHAR(255) NOT NULL,
    expires_at DATETIME NOT NULL,
    used BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT FK_prt_user FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_prt_token (token)
);

</parameter>
