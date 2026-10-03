-- V24: Add academic_semesters as the single source of truth for semester data.
-- Clazz and RegistrationPeriod will now reference this table via FK.
-- Enrollment semester/academic_year are derived from Clazz — no longer stored directly.
-- Users get a cohort_year column for curriculum-aware recommendations.

-- ──────────────────────────────────────────────
-- 1. ACADEMIC SEMESTERS TABLE
-- ──────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS academic_semesters (
    id          BIGSERIAL PRIMARY KEY,
    code        VARCHAR(30) NOT NULL UNIQUE,   -- e.g. "2026-2027-1"
    name        VARCHAR(150) NOT NULL,          -- e.g. "Học kỳ 1 năm học 2026-2027"
    academic_year VARCHAR(20) NOT NULL,         -- e.g. "2026-2027"
    semester_no INTEGER NOT NULL,               -- 1, 2 (or 3 for summer)
    start_date  DATE NOT NULL,
    end_date    DATE NOT NULL,
    status      VARCHAR(20) NOT NULL DEFAULT 'UPCOMING', -- UPCOMING | ACTIVE | CLOSED
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Seed known semesters from existing data
INSERT INTO academic_semesters (code, name, academic_year, semester_no, start_date, end_date, status)
VALUES
    ('2025-2026-1', 'Học kỳ 1 năm học 2025-2026', '2025-2026', 1, '2025-09-01', '2026-01-10', 'CLOSED'),
    ('2025-2026-2', 'Học kỳ 2 năm học 2025-2026', '2025-2026', 2, '2026-01-20', '2026-05-31', 'CLOSED'),
    ('2026-2027-1', 'Học kỳ 1 năm học 2026-2027', '2026-2027', 1, '2026-09-01', '2027-01-15', 'ACTIVE')
ON CONFLICT (code) DO NOTHING;

-- ──────────────────────────────────────────────
-- 2. LINK classes → academic_semesters
-- ──────────────────────────────────────────────
ALTER TABLE classes ADD COLUMN IF NOT EXISTS semester_id BIGINT REFERENCES academic_semesters(id) ON DELETE SET NULL;

-- Migrate existing string-based data to FK
UPDATE classes
SET semester_id = (SELECT id FROM academic_semesters WHERE code = '2026-2027-1')
WHERE (semester = 'HK1' AND academic_year = '2026-2027')
   OR (semester = 'HK1' AND academic_year IS NULL);

UPDATE classes
SET semester_id = (SELECT id FROM academic_semesters WHERE code = '2025-2026-2')
WHERE semester = 'HK2' AND academic_year = '2025-2026';

-- ──────────────────────────────────────────────
-- 3. LINK registration_periods → academic_semesters
-- ──────────────────────────────────────────────
ALTER TABLE registration_periods ADD COLUMN IF NOT EXISTS semester_id BIGINT REFERENCES academic_semesters(id) ON DELETE SET NULL;

UPDATE registration_periods
SET semester_id = (SELECT id FROM academic_semesters WHERE code = '2026-2027-1')
WHERE semester = 'HK1' AND academic_year = '2026-2027';

UPDATE registration_periods
SET semester_id = (SELECT id FROM academic_semesters WHERE code = '2025-2026-2')
WHERE semester = 'HK2' AND academic_year = '2025-2026';

-- ──────────────────────────────────────────────
-- 4. Add cohort_year to users (sinh viên thuộc khóa nào)
-- ──────────────────────────────────────────────
ALTER TABLE users ADD COLUMN IF NOT EXISTS cohort_year INTEGER;

-- Infer cohort from existing student_code pattern (SV2024xxxx → 2024)
UPDATE users
SET cohort_year = CAST(SUBSTRING(student_code FROM 3 FOR 4) AS INTEGER)
WHERE role = 'STUDENT'
  AND student_code IS NOT NULL
  AND LENGTH(student_code) >= 6
  AND student_code ~ '^SV[0-9]{4}';
