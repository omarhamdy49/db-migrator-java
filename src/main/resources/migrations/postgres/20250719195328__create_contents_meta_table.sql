-- tag: contents_v1

-- up
CREATE TABLE contents_metadata
(
    id                 BIGSERIAL PRIMARY KEY,
    content_id         UUID  Unique  REFERENCES contents (id) ON DELETE CASCADE,

    imdb_id            VARCHAR(20),   -- e.g., "tt1375666"
    imdb_rate          NUMERIC(3, 1), -- e.g., 8.8

    subtitle_languages JSONB,         -- ["en", "ar", "fr"]
    audio_languages    JSONB,         -- ["en", "de"]
    total_user_reviews BIGINT           DEFAULT 0,
    users_rate         NUMERIC(3, 2) DEFAULT 0.0,
    total_views        BIGINT        DEFAULT 0,
    avg_watching_time  BIGINT, -- in seconds or minutes

    scheduling_notes   TEXT
);

-- down
DROP TABLE IF EXISTS contents_metadata;
