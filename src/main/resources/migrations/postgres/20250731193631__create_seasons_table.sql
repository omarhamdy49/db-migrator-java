-- tag: contents_v1

-- up
CREATE TABLE seasons
(
    id            UUID PRIMARY KEY  DEFAULT uuid_generate_v4(),
    content_id    UUID REFERENCES contents (id) ON DELETE CASCADE,
    season_number INT         NOT NULL, -- starts from 1
    title         TEXT,
    slug          TEXT        NOT NULL UNIQUE,
    status        VARCHAR(10) NOT NULL DEFAULT 'draft',
    excerpt       varchar(255),
    description   TEXT,

    media         JSONB,                -- [{ "type": "trailer", "streaming_url": "https://..." }]
    is_special    BOOLEAN              DEFAULT FALSE,
    is_featured   BOOLEAN              DEFAULT FALSE,
    is_free       BOOLEAN              DEFAULT FALSE,
    regions       TEXT[],
    keywords      TEXT[],
    release_date  DATE,
    created_at    TIMESTAMP            DEFAULT now(),
    updated_at    TIMESTAMP            DEFAULT now(),
    UNIQUE (content_id, season_number)
);
-- down
DROP TABLE IF EXISTS seasons;