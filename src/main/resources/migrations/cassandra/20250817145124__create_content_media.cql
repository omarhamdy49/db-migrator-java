-- tag: 20250817145124

-- up
CREATE TABLE IF NOT EXISTS content_media
(
    ref_id     uuid,            -- partition key (one content’s media lives together)
    slug       text,
    type       text,            -- clustering (e.g., "video","audio","subtitle")
    uri        text,
    status     text,
    streaming  map<text, text>,
    size       bigint,          -- file size in bytes
    dimensions text,            -- e.g. "1920x1080"
    file_name  text,
    media_type text,
    mime_type  text,
    extra      map<text, text>, -- for extensibility (e.g. color_profile, orientation, etc.)
    created_at timestamp,
    updated_at timestamp,
    PRIMARY KEY (ref_id, type,media_type)
);
-- down
DROP TABLE IF EXISTS content_media;