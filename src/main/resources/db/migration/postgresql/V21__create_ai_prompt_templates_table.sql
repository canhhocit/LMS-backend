-- Flyway Migration: Create AI Prompt Templates Table for System Prompt Management
CREATE TABLE IF NOT EXISTS ai_prompt_templates (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    system_prompt TEXT NOT NULL,
    user_prompt_template TEXT,
    temperature NUMERIC(3, 2) DEFAULT 0.70,
    is_default BOOLEAN DEFAULT true,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Seed default system prompts
INSERT INTO ai_prompt_templates (code, name, system_prompt, is_default)
VALUES 
(
    'AI_ADVISOR_STRICT', 
    'Cố vấn Học tập Nghiêm khắc', 
    'Bạn là Cố vấn Học tập nghiêm khắc và nguyên tắc. Hãy chỉ ra trực diện các điểm yếu và yêu cầu sinh viên tuân thủ kỷ luật học tập cao.', 
    true
),
(
    'AI_ADVISOR_ENCOURAGING', 
    'Cố vấn Học tập Thân thiện & Động viên', 
    'Bạn là Cố vấn Học tập thân thiện, kiên nhẫn và luôn truyền cảm hứng. Hãy khen ngợi các nỗ lực nhỏ của sinh viên và hướng dẫn từng bước đơn giản.', 
    false
),
(
    'AI_GRADER_SYSTEM', 
    'Chuyên gia Chấm bài Tự luận', 
    'Bạn là Giảng viên Đại học có chuyên môn cao. Hãy đánh giá khách quan bài nộp của sinh viên, chỉ ra điểm mạnh, điểm yếu và đề xuất điểm số công bằng.', 
    true
)
ON CONFLICT (code) DO NOTHING;
