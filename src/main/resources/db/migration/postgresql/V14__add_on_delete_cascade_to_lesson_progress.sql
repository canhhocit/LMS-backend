ALTER TABLE lesson_progress DROP CONSTRAINT IF EXISTS lesson_progress_lesson_id_fkey;
ALTER TABLE lesson_progress ADD CONSTRAINT lesson_progress_lesson_id_fkey FOREIGN KEY (lesson_id) REFERENCES lessons(id) ON DELETE CASCADE;
