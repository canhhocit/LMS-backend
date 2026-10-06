CREATE TABLE system_error_logs (
    id BIGSERIAL PRIMARY KEY,
    actor_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    actor_email VARCHAR(100),
    exception_type VARCHAR(255) NOT NULL,
    error_message TEXT,
    request_method VARCHAR(10),
    request_path VARCHAR(2048),
    stack_trace TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_system_error_logs_created ON system_error_logs(created_at DESC);
CREATE INDEX idx_system_error_logs_actor ON system_error_logs(actor_id);

INSERT INTO admin_permissions (code, description)
VALUES ('VIEW_SYSTEM_LOGS', 'Xem nhật ký lỗi và sự cố hệ thống')
ON CONFLICT (code) DO NOTHING;

INSERT INTO user_admin_permissions (user_id, permission_id)
SELECT uap.user_id, p.id
FROM user_admin_permissions uap
JOIN admin_permissions existing_permission ON existing_permission.id = uap.permission_id
JOIN admin_permissions p ON p.code = 'VIEW_SYSTEM_LOGS'
WHERE existing_permission.code = 'SYSTEM_CONFIG'
ON CONFLICT DO NOTHING;
