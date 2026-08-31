ALTER TABLE submissions
    ADD COLUMN IF NOT EXISTS submission_type VARCHAR(30) NOT NULL DEFAULT 'FILE',
    ADD COLUMN IF NOT EXISTS file_urls TEXT,
    ADD COLUMN IF NOT EXISTS external_link VARCHAR(500);

UPDATE submissions
SET submission_type = 'FILE'
WHERE submission_type IS NULL OR submission_type = '';

CREATE INDEX IF NOT EXISTS idx_submissions_type ON submissions(submission_type);
