-- tag: contents_v1

-- up
CREATE TABLE episodes
(
    id             UUID PRIMARY KEY  DEFAULT uuid_generate_v4(),
    season_id      UUID REFERENCES seasons (id) ON DELETE CASCADE,
    episode_number INT  NOT NULL, -- starts from 1
    title          TEXT,
    slug           TEXT NOT NULL UNIQUE,
    status        VARCHAR(10) NOT NULL DEFAULT 'draft',
    excerpt        varchar(255),
    description    TEXT,
    duration       INT,           -- in seconds
    thumbnail_url  VARCHAR(255),
    hero_url       VARCHAR(255),
    streaming      JSONB,
    subtitles      JSONB,         -- { "en": "url", "ar": "url" }
    audio          JSONB,         -- { "en": "url", "ar": "url" }
    skips          JSONB,         -- { "intro": [0, 60], "recap": [100, 140] }
    release_date   TIMESTAMP,
    is_free        BOOLEAN   DEFAULT FALSE,
    regions        TEXT[],
    keywords       TEXT[],
    created_at     TIMESTAMP DEFAULT now(),
    updated_at     TIMESTAMP DEFAULT now(),
    UNIQUE (season_id, episode_number)
);

-- down
DROP TABLE IF EXISTS episodes;