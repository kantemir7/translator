CREATE TABLE IF NOT EXISTS translation_requests (
    id SERIAL PRIMARY KEY,
    ip_address VARCHAR(255) NOT NULL,
    source_text TEXT NOT NULL,
    translated_text TEXT,
    request_time TIMESTAMP NOT NULL
);