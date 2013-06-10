package audioplayer.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import audioplayer.player.codec.AudioFile;
import java.io.File;

/**
 *  LoLPlayer II - Audio-Player Project
 * 
 * @author Oliver Daus
 * 
 */
public class LoLPlayerDB extends DataBase{

	public static final String table_author = "author";
	public static final String table_album = "album";
	public static final String table_filepath = "filepath";
	public static final String table_genre = "genre";
	public static final String table_playlist = "playlist";
	public static final String table_rating = "rating";
	public static final String table_statistics = "statistics";
	public static final String table_title = "title";
	
	public static final String view_playlistview = "playlistview";
	
	public PlaylistItem[] getPlaylistItems() throws SQLException{
		ResultSet rs = getConnection().getValues("playlistview");
		
		ArrayList<PlaylistItem> pi = new ArrayList<LoLPlayerDB.PlaylistItem>();
		
		while(rs.next()){
			pi.add(new PlaylistItem(
					rs.getInt("songid"),
					rs.getInt("no"),
					rs.getString("filepath"),
					rs.getString("title"),
					rs.getString("author")));
		}
		
		PlaylistItem[] rpli = new PlaylistItem[pi.size()];
		return pi.toArray(rpli);
	}
        
        
	
	public void addAudioFile(AudioFile af){
            //AudioFile naf = getAudioFilebyFilepath();
	}
	
        public AudioFile getAudioFilebyFilepath(String filepath){
            String sql =  "SELECT mediafile.*"
                        + "FROM mediafile "
                        + "WHERE mediafile.path = '%s';";
            try {
                ResultSet rs = getConnection().sendQuery(String.format(sql, new File(filepath).toString()));
                
                AudioFile af = getAudioFile(rs);
                if (af == null){
                    rs = getConnection().sendQuery(String.format(sql, new File(filepath).getAbsolutePath()));
                    af = getAudioFile(rs);
                }
                
                return af;    
            } catch (SQLException ex) {
                return null;
            }
	}
        
        private AudioFile getAudioFile(ResultSet rs){
            try {
                if (!rs.next()) return null;
                
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
        
        
	public class PlaylistItem{
		
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
