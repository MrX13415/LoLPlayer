-- =============================================
--
--  LOL-Player II
--
--  Author: Oliver Daus
--
--
--  PostgresSQL "views-create" commands
--
-- =============================================
	   
CREATE OR REPLACE VIEW playlistview AS 
 SELECT playlist.songid, playlist.no, filepath.filepath, title.title, 
    playlist.author
   FROM filepath
   JOIN (title
   JOIN (author
   JOIN playlist ON public.playlist.songid = author.id) playlist ON playlist.songid = title.id) ON playlist.songid = filepath.id;