-- tag: contents_v1

-- up
CREATE TABLE genres
(
    id         UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name       VARCHAR(255) NOT NULL UNIQUE,
    excerpt    varchar(255),
    created_at TIMESTAMP        DEFAULT now(),
    updated_at TIMESTAMP        DEFAULT now()
);
-- down
DROP TABLE IF EXISTS genres;