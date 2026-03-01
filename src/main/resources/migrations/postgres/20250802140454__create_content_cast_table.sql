-- tag: contents_v1

-- up
CREATE TABLE content_cast
(
    content_id     UUID         NOT NULL REFERENCES contents (id) ON DELETE CASCADE,
    person_id      UUID         NOT NULL REFERENCES people (id) ON DELETE CASCADE,
    role           VARCHAR(255) NOT NULL, -- e.g., actor, director
    order_index    INT, -- e.g., 1 for main actor, 2 for a supporting actor
    character_name VARCHAR(255),
    PRIMARY KEY (content_id, person_id, role)
);
-- down
-- drop the correct table on rollback
DROP TABLE IF EXISTS content_cast;