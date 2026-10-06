ALTER TABLE tuition_rates
    ADD COLUMN IF NOT EXISTS semester VARCHAR(20),
    ADD COLUMN IF NOT EXISTS effective_from DATE;

UPDATE tuition_rates
SET effective_from = CURRENT_DATE
WHERE effective_from IS NULL;

ALTER TABLE tuition_rates
    ALTER COLUMN effective_from SET NOT NULL;

ALTER TABLE tuition_rates
    DROP CONSTRAINT IF EXISTS tuition_rates_academic_year_key;

CREATE UNIQUE INDEX IF NOT EXISTS uk_tuition_rates_scope_effective
    ON tuition_rates (academic_year, (COALESCE(semester, '')), effective_from);
