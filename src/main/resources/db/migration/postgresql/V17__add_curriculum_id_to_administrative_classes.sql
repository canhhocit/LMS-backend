ALTER TABLE administrative_classes ADD COLUMN IF NOT EXISTS curriculum_id BIGINT;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'fk_administrative_classes_curriculum'
    ) THEN
        ALTER TABLE administrative_classes
        ADD CONSTRAINT fk_administrative_classes_curriculum
        FOREIGN KEY (curriculum_id) REFERENCES curricula(id) ON DELETE SET NULL;
    END IF;
END $$;
