-- tag: uuid-ext

-- up
-- Write your up migration here
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
-- down
-- Write your down migration here
DROP EXTENSION IF EXISTS "uuid-ossp";