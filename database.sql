-- Create database if not exist (optional)
CREATE DATABASE IF NOT EXISTS lifestealsmp;

-- Switch to database
USE lifestealsmp;

-- Create player_hearts table
CREATE TABLE IF NOT EXISTS player_hearts (
    uuid VARCHAR(36) NOT NULL PRIMARY KEY,
    hearts INT NOT NULL
);

-- Optional: Create initial demo data
-- INSERT INTO player_hearts (uuid, hearts) VALUES ('00000000-0000-0000-0000-000000000000', 20);
