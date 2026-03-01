-- tag: contents_v1

-- up
CREATE TABLE people
(
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name        VARCHAR(255) NOT NULL,
    slug        VARCHAR(255) NOT NULL UNIQUE,
    role        VARCHAR(100) NOT NULL, -- e.g., actor, director
    avatar_url  VARCHAR(255),
    nationality VARCHAR(255),
    gender      VARCHAR(20),
    birth_date  DATE,
    bio         VARCHAR(255),
    description TEXT,
    created_at  TIMESTAMP        DEFAULT now(),
    updated_at  TIMESTAMP        DEFAULT now()
);
-- down
DROP TABLE IF EXISTS people;