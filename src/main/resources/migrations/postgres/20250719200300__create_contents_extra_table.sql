-- tag: contents_v1

-- up
CREATE TABLE contents_extra
(
    id                 BIGSERIAL PRIMARY KEY,
    content_id         UUID Unique REFERENCES contents (id) ON DELETE CASCADE,
    description        TEXT,
    keywords           TEXT[], -- e.g., ["sci-fi", "dream", "thriller"]
    banner_url         VARCHAR(255),
    hero_banner_url    VARCHAR(255),
    -- intro and recap defined as JSON object with mm:ss formatted strings
    skips              JSONB,  -- e.g., [{"name": "intro", "start": "00:00", "end": "01:30"}, {"name": "recap", "start": "01:30", "end": "02:00"}]

    media              JSONB,  -- [{ "type": "trailer", "streaming_url": "https://..." }]
    streaming          JSONB,  -- [{ "type": "free", "streaming_url": "free_quality_url" }]
    production_company VARCHAR(150),
    vendor             JSONB,
    created_by         JSONB
);

CREATE INDEX idx_contents_extra_keywords ON contents_extra USING GIN (keywords);
-- down
-- drop the correct table on rollback
DROP TABLE IF EXISTS contents_extra;