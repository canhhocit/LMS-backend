CREATE TABLE IF NOT EXISTS registration_period_classes (
    period_id BIGINT NOT NULL REFERENCES registration_periods(id) ON DELETE CASCADE,
    clazz_id BIGINT NOT NULL REFERENCES classes(id) ON DELETE CASCADE,
    CONSTRAINT pk_registration_period_classes PRIMARY KEY (period_id, clazz_id)
);

CREATE INDEX IF NOT EXISTS idx_registration_period_classes_clazz
    ON registration_period_classes (clazz_id);
