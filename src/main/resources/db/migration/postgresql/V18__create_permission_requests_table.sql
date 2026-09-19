-- V18__create_permission_requests_table.sql
CREATE TABLE IF NOT EXISTS permission_requests (
    id BIGSERIAL PRIMARY KEY,
    lecturer_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    permission_type VARCHAR(50) NOT NULL,
    class_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    reason TEXT,
    approved_by BIGINT REFERENCES users(id) ON DELETE SET NULL,
    valid_until TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_perm_req_lecturer ON permission_requests(lecturer_id);
CREATE INDEX IF NOT EXISTS idx_perm_req_class ON permission_requests(class_id);
CREATE INDEX IF NOT EXISTS idx_perm_req_status ON permission_requests(status);
