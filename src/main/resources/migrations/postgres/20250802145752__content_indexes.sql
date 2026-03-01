-- tag: 20250802145752

-- up
CREATE INDEX contents_region_idx ON contents USING GIN (regions);
CREATE INDEX seasons_region_idx ON seasons USING GIN (regions);
CREATE INDEX episodes_region_idx ON episodes USING GIN (regions);
-- down
-- drop the indexes on rollback
DROP INDEX IF EXISTS contents_region_idx;
DROP INDEX IF EXISTS seasons_region_idx;
DROP INDEX IF EXISTS episodes_region_idx;