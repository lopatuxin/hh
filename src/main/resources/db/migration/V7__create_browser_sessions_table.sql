CREATE TABLE browser_sessions (
    id          BIGSERIAL PRIMARY KEY,
    state_json  TEXT NOT NULL,
    created_at  TIMESTAMP NOT NULL,
    updated_at  TIMESTAMP NOT NULL
);
