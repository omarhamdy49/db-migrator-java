-- tag: contents_v1

-- up
CREATE TABLE content_genres
(
    content_id UUID NOT NULL REFERENCES contents (id) ON DELETE CASCADE,
    -- reference the proper genres table instead of the non‑existent "genre"
    genre_id   UUID NOT NULL REFERENCES genres (id) ON DELETE CASCADE,
    PRIMARY KEY (content_id, genre_id)
);
-- down
DROP TABLE IF EXISTS content_genres;