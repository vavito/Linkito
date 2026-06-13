-- Flyway baseline migration creating core tables (simple and matching the spec)
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS short_link (
    id UUID PRIMARY KEY,
    title VARCHAR(150),
    original_url TEXT NOT NULL,
    short_code VARCHAR(10) UNIQUE NOT NULL,
    click_count BIGINT DEFAULT 0,
    active BOOLEAN DEFAULT TRUE,
    user_id UUID REFERENCES users(id),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS link_click (
    id UUID PRIMARY KEY,
    short_link_id UUID REFERENCES short_link(id) ON DELETE CASCADE,
    ip_address VARCHAR(45),
    user_agent TEXT,
    clicked_at TIMESTAMP
);
