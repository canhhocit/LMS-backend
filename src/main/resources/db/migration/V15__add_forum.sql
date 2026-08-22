-- V15: Add forum tables for class discussions
CREATE TABLE forum_posts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    clazz_id BIGINT NOT NULL,
    author_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT FK_fp_clazz FOREIGN KEY (clazz_id) REFERENCES classes(id),
    CONSTRAINT FK_fp_author FOREIGN KEY (author_id) REFERENCES users(id),
    INDEX idx_fp_clazz (clazz_id)
);

CREATE TABLE forum_comments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    post_id BIGINT NOT NULL,
    author_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT FK_fc_post FOREIGN KEY (post_id) REFERENCES forum_posts(id),
    CONSTRAINT FK_fc_author FOREIGN KEY (author_id) REFERENCES users(id),
    INDEX idx_fc_post (post_id)
);

