-- tag: contents_v1

-- up
-- Optional ENUM type (PostgreSQL native ENUM)

CREATE TABLE contents
(
    id              UUID PRIMARY KEY     DEFAULT uuid_generate_v4(),
    type            VARCHAR(20),
    title           TEXT        NOT NULL,
    slug            TEXT        NOT NULL UNIQUE, -- Unique for SEO/permalinks
    thumbnail_url   VARCHAR(255),
    hero_url        VARCHAR(255),
    excerpt         VARCHAR(255),
    release_year    INT         NOT NULL,
    duration        INT,
    language        VARCHAR(10) NOT NULL,
    -- List of country codes (e.g., ["US", "EG", "DE"])
    regions         TEXT[],
    maturity_rating VARCHAR(10),
    status          VARCHAR(10) NOT NULL DEFAULT 'draft',
    is_featured     BOOLEAN              DEFAULT FALSE,
    is_free         BOOLEAN              DEFAULT FALSE,
    publish_date    TIMESTAMP,
    expiry_date     TIMESTAMP,
    created_at      TIMESTAMP            DEFAULT now(),
    updated_at      TIMESTAMP            DEFAULT now()
);
CREATE INDEX idx_contents_region ON contents USING GIN (regions);

-- down
DROP TABLE IF EXISTS contents;