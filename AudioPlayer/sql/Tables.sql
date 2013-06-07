-- =============================================
--
--  LOL-Player II
--
--  Author: Oliver Daus
--
--
--  PostgresSQL "create-table" commands
--
-- =============================================
	   
-- Tabelle Filepath
CREATE TABLE Filepath (
  ID            serial                     PRIMARY KEY,             -- Song ID
  FilePath      varChar(255)   NOT NULL                             -- The path to the song
);

-- Tabelle Title
CREATE TABLE Title (
  ID            serial                     PRIMARY KEY,             -- Song ID
  Title         varchar(100)                                        -- One or more titles
);


-- Tabelle Author
CREATE TABLE Author (
  ID            serial                     PRIMARY KEY,             -- Song ID
  Author        varchar(100)                                        -- One or more authors
);


-- Tabelle Album
CREATE TABLE Album (
  ID            serial                     PRIMARY KEY,             -- Song ID
  Album         varchar(100)                                        -- One or more albums
);


-- Tabelle Genre
CREATE TABLE Genre (
  ID            serial                     PRIMARY KEY,             -- Song ID
  Genre         varchar(100)                                        -- One or more genres
);


-- Tabelle Statistics
CREATE TABLE Statistics (
  ID            serial                     PRIMARY KEY,             -- Song ID
  Frequency     smallint       NOT NULL                             -- The frequency of the Song
);


-- Tabelle Playlist
CREATE TABLE Playlist (
  No            serial                     PRIMARY KEY,             -- Position in the Playlist
  SongID        int            NOT NULL                             -- ID of the Song
);


-- Tabelle Rating
CREATE TABLE Rating (
  ID            serial                     PRIMARY KEY,             -- Song ID
  Rate          smallint       NOT NULL                             -- Raiting from 0 to 5
);


