package audioplayer.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import audioplayer.database.sql.DataBase;
import audioplayer.player.AudioFile;

import java.io.File;

/**
 * LoLPlayer II - Audio-Player Project
 * 
 * @author Oliver Daus / Luca Madonia
 * 
 */
public class NLoLPlayerDB extends DataBase {

	public HashMap<Integer, AudioFile> getPlaylistItems() throws SQLException {
		
		return null;
	}

	public void clearPlaylist(){

	}
	
	public void addPlaylistItem(AudioFile af) throws SQLException{
		
	}
	
	public int addAudioFile(AudioFile af) throws SQLException {
		AudioFile naf = getAudioFilebyFilepath(af.getFile().toString());
		if (naf != null)
			return naf.getId();
		int fileid = addFile(af.getFile().toString());
		int titleid = addTitle(af.getTitle());
		int authorid = addAuthor(af.getAuthor());
		int albumid = addAlbum(af.getAlbum());
		int genreid = addGenre(af.getGenre());
		return addMedia(fileid, titleid, authorid, albumid, genreid);
	}

	public int addMedia(int fileid, int titleid, int authorid, int albumid,
			int genreid) throws SQLException {
		String sql = String
				.format("INSERT INTO media(fileid,titleid,authorid,albumid,genreid) VALUES(%s,%s,%s,%s,%s);",
						fileid, titleid, authorid, albumid, genreid);
		
		if (getAudioFilebyFilepath(getFile(fileid)) == null)
			getConnection().sendSQLUpdate(sql);
		
		int songid = getSongID(fileid);
		setStatistic(songid, 0);
		setRating(songid, 0);
		try{
			return getAudioFilebyFilepath(getFile(fileid)).getId();
		}catch(Exception e){
			return -1;
		}
	}

	public void setStatistic(int songid, int frequency) throws SQLException {
		String sql = String.format(
				"INSERT INTO statistic(songid, frequency) VALUES(%s,%s);",
				songid, frequency);
		if (getStatistic(songid) < 0)
			getConnection().sendSQLUpdate(sql);
	}

	public int getStatistic(int songid) {
		String sql = String.format(
				"SELECT * FROM statistic WHERE statistic.songid = %s;", songid);
		try {
			ResultSet rs = getConnection().sendSQLQuery(sql);
			rs.next();
			return rs.getInt("frequency");
		} catch (SQLException ex) {
		}
		return -1;
	}

	public void setRating(int songid, int rate) throws SQLException {
		String sql = String
				.format("INSERT INTO rating(songid, rate) VALUES(%s,%s);",
						songid, rate);
		if (getRating(songid) < 0)
			getConnection().sendSQLUpdate(sql);
	}

	public int getRating(int songid) {
		String sql = String.format(
				"SELECT * FROM rating WHERE rating.songid = %s;", songid);
		try {
			ResultSet rs = getConnection().sendSQLQuery(sql);
			rs.next();
			return rs.getInt("rate");
		} catch (SQLException ex) {
		}
		return -1;
	}

	public int addFile(String s) {
		String sql = String.format("INSERT INTO file(path) VALUES('%s');", s);
		if (getFileID(s) == -1)
			try {
				getConnection().sendSQLUpdate(sql);
			} catch (SQLException ex) {
			}
		return getFileID(s);
	}

	public int addTitle(String s) {
		String sql = String.format("INSERT INTO title(title) VALUES('%s');", s);
		if (getTitleID(s) == -1)
			try {
				getConnection().sendSQLUpdate(sql);
			} catch (SQLException ex) {
			}
		return getTitleID(s);
	}

	public int addAuthor(String s) {
		String sql = String.format("INSERT INTO author(author) VALUES('%s');",
				s);
		if (getAuthorID(s) == -1)
			try {
				getConnection().sendSQLUpdate(sql);
			} catch (SQLException ex) {
			}
		return getAuthorID(s);
	}

	public int addAlbum(String s) {
		String sql = String.format("INSERT INTO album(album) VALUES('%s');", s);
		if (getAlbumID(s) == -1)
			try {
				getConnection().sendSQLUpdate(sql);
			} catch (SQLException ex) {
			}
		return getAlbumID(s);
	}

	public int addGenre(String s) {
		String sql = String.format("INSERT INTO genre(genre) VALUES('%s');", s);
		if (getGenreID(s) == -1)
			try {
				getConnection().sendSQLUpdate(sql);
			} catch (SQLException ex) {
			}
		return getGenreID(s);
	}

	public AudioFile getAudioFilebyFilepath(String filepath) {
		String sql = "SELECT mediafile.*" + "FROM mediafile "
				+ "WHERE mediafile.path = '%s';";
		try {
			ResultSet rs = getConnection().sendSQLQuery(
					String.format(sql, new File(filepath).toString()));

			AudioFile af = getAudioFile(rs);
			if (af == null) {
				rs = getConnection()
						.sendSQLQuery(
								String.format(sql,
										new File(filepath).getAbsolutePath()));
				af = getAudioFile(rs);
			}

			return af;
		} catch (Exception ex) {
			return null;
		}
	}

	private AudioFile getAudioFile(ResultSet rs) {
		try {
			rs.next();

			AudioFile af = new AudioFile(new File(rs.getString("path")));
			af.setTitle(rs.getString("title"));
			af.setAuthor(rs.getString("author"));
			af.setAlbum(rs.getString("album"));
			af.setGenre(rs.getString("genre"));
			af.setRating(rs.getInt("rate"));
			af.setFrequency(rs.getInt("frequency"));
			af.setId(rs.getInt("id"));

			return af;
		} catch (SQLException ex) {
			return null;
		}
	}

	public int getSongID(int fileid) {
		String sql = String.format(
				"SELECT media.* FROM media WHERE media.fileid = '%s'", fileid);
		try {
			ResultSet rs = getConnection().sendSQLQuery(sql);
			rs.next();
			return rs.getInt("id");
		} catch (SQLException ex) {
		}
		return -1;
	}

	public int getFileID(String filepath) {
		String sql = "SELECT *" + "FROM file " + "WHERE file.path = '%s';";
		try {
			ResultSet rs = getConnection().sendSQLQuery(
					String.format(sql, filepath));
			rs.next();
			return rs.getInt("id");
		} catch (SQLException ex) {
			return -1;
		}
	}

	public int getTitleID(String title) {
		String sql = "SELECT *" + "FROM title " + "WHERE title.title = '%s';";
		try {
			ResultSet rs = getConnection().sendSQLQuery(String.format(sql, title));
			rs.next();
			return rs.getInt("id");
		} catch (SQLException ex) {
			return -1;
		}
	}

	public int getAuthorID(String author) {
		String sql = "SELECT *" + "FROM author "
				+ "WHERE author.author = '%s';";
		try {
			ResultSet rs = getConnection()
					.sendSQLQuery(String.format(sql, author));
			rs.next();
			return rs.getInt("id");
		} catch (SQLException ex) {
			return -1;
		}
	}

	public int getAlbumID(String album) {
		String sql = "SELECT *" + "FROM album " + "WHERE album.album = '%s';";
		try {
			ResultSet rs = getConnection().sendSQLQuery(String.format(sql, album));
			rs.next();
			return rs.getInt("id");
		} catch (SQLException ex) {
			return -1;
		}
	}

	public int getGenreID(String genre) {
		String sql = "SELECT *" + "FROM genre " + "WHERE genre.genre = '%s';";
		try {
			ResultSet rs = getConnection().sendSQLQuery(String.format(sql, genre));
			rs.next();
			return rs.getInt("id");
		} catch (SQLException ex) {
			return -1;
		}
	}

	public String getFile(int id) {
		String sql = "SELECT *" + "FROM file " + "WHERE file.id = '%s';";
		try {
			ResultSet rs = getConnection().sendSQLQuery(String.format(sql, id));
			rs.next();
			return rs.getString("path");
		} catch (SQLException ex) {
			return null;
		}
	}

	public String getTitle(int id) {
		String sql = "SELECT *" + "FROM title " + "WHERE title.id = '%s';";
		try {
			ResultSet rs = getConnection().sendSQLQuery(String.format(sql, id));
			rs.next();
			return rs.getString("title");
		} catch (SQLException ex) {
			return null;
		}
	}

	public String getAuthor(int id) {
		String sql = "SELECT *" + "FROM author " + "WHERE author.id = '%s';";
		try {
			ResultSet rs = getConnection().sendSQLQuery(String.format(sql, id));
			rs.next();
			return rs.getString("author");
		} catch (SQLException ex) {
			return null;
		}
	}

	public String getAlbum(int id) {
		String sql = "SELECT *" + "FROM album " + "WHERE album.id = '%s';";
		try {
			ResultSet rs = getConnection().sendSQLQuery(String.format(sql, id));
			rs.next();
			return rs.getString("album");
		} catch (SQLException ex) {
			return null;
		}
	}

	public String getGenre(int id) {
		String sql = "SELECT *" + "FROM genre " + "WHERE genre.id = '%s';";
		try {
			ResultSet rs = getConnection().sendSQLQuery(String.format(sql, id));
			rs.next();
			return rs.getString("genre");
		} catch (SQLException ex) {
			return null;
		}
	}
	
	public void updateFrequency(int id) throws SQLException {                     
		getConnection().sendSQLUpdate("UPDATE statistic SET frequency = frequency + 1 WHERE songid = " + id);
    }

	public class PlaylistItem {

		private int songid;
		private int no;
		private String filepath;
		private String title;
		private String author;

		public PlaylistItem(int songid, int no, String filepath, String title,
				String author) {
			super();
			this.songid = songid;
			this.no = no;
			this.filepath = filepath;
			this.title = title;
			this.author = author;
		}

		public int getSongid() {
			return songid;
		}

		public int getNo() {
			return no;
		}

		public String getFilepath() {
			return filepath;
		}

		public String getTitle() {
			return title;
		}

		public String getAuthor() {
			return author;
		}

	}
}
