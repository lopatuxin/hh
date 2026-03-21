CREATE TABLE apply_runs (
    id         BIGSERIAL PRIMARY KEY,
    started_at TIMESTAMP NOT NULL,
    finished_at TIMESTAMP,
    found      INT NOT NULL DEFAULT 0,
    filtered   INT NOT NULL DEFAULT 0,
    applied    INT NOT NULL DEFAULT 0,
    failed     INT NOT NULL DEFAULT 0,
    duration_ms BIGINT NOT NULL DEFAULT 0
);
