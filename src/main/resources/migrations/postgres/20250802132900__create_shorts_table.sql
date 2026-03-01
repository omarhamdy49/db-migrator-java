-- tag: contents_v1

-- up
CREATE TABLE shorts
(
    id              UUID PRIMARY KEY  DEFAULT uuid_generate_v4(),
    season_id       UUID REFERENCES seasons (id) ON DELETE CASCADE,
    related_episode UUID REFERENCES episodes (id),
    type            VARCHAR(30) CHECK (type IN ('recap', 'trailer', 'bts', 'extra')),
    title           TEXT,
    status          VARCHAR(10) NOT NULL DEFAULT 'draft',
    duration        INT,
    thumbnail_url   VARCHAR(255),
    streaming       VARCHAR(255),
    is_free         BOOLEAN              DEFAULT FALSE,
    release_date    TIMESTAMP,
    regions         TEXT[],
    keywords        TEXT[],
    language        VARCHAR(10),
    created_at      TIMESTAMP            DEFAULT now()
);

-- down
DROP TABLE IF EXISTS shorts;