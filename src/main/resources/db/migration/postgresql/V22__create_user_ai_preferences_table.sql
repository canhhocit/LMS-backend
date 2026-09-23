-- Flyway Migration: Create User AI Preferences Table for User Prompt Personalization
CREATE TABLE IF NOT EXISTS user_ai_preferences (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    preferred_call_name VARCHAR(100),
    ai_tone VARCHAR(100) DEFAULT 'FRIENDLY',
    custom_tone_description TEXT,
    response_length VARCHAR(50) DEFAULT 'DETAILED',
    custom_context TEXT,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);
