package database_connection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import models.Album;
import models.Playlist;
import models.Song;

public class DBUtils {

	// LIST TOP SONGS
	public static List<Song> top_songs(Connection conn) throws SQLException {
		String sql = "SELECT S.title, S.year, S.duration, descr, A.first_name, A.last_name, AL.title"
				+ "FROM song AS S INNER JOIN has AS H ON S.id = H.id_song"
				+ "INNER JOIN artist AS A ON A.id = H.id_artist" + "LEFT JOIN album AS AL ON AL.id = H.id_album"
				+ "INNER JOIN genre AS G ON G.id = S.id_genre" + "INNER JOIN artist AS AR ON AR.id = AL.id_artist"
				+ "WHERE rate_top = TRUE;";

		PreparedStatement pstm = conn.prepareStatement(sql);

		ResultSet rs = pstm.executeQuery();
		List<Song> list = new ArrayList<Song>();
		ListIterator<Song> cursor = list.listIterator();
		String previous_title = "";

		while (rs.next()) {

			String title = rs.getString("S.title");
			String first_name_artist = rs.getString("first_name");
			String last_name_artist = rs.getString("last_name");
			String artist = first_name_artist + " " + last_name_artist;

			// add artist to previous song
			if (title == previous_title) {
				Song previous_song = cursor.previous();
				previous_song.addArtist(artist);
				cursor.next();
			}

			else {

				float duration = rs.getFloat("S.duration");
				String genre = rs.getString("descr");
				int year = rs.getInt("S.year");
				String album = rs.getString("AL.title");

				if (rs.wasNull()) { // if song is a single
					album = " ";
				}

				Song best_song = new Song();
				best_song.setTitle(title);
				best_song.setDuration(duration);
				best_song.setGenre(genre);
				best_song.addArtist(artist);
				best_song.setAlbum(album);
				best_song.setYear(year);
				best_song.setIs_top(true);

				list.add(best_song);
				cursor.next();
			}

			previous_title = title;
		}

		if (list.size() > 0) {
			return list;
		}

		return null;
	}

	// LIST ALL SONGS
	public static List<Song> listSongs(Connection conn) throws SQLException {
		String sql = "SELECT S.title, S.year, S.duration, descr, A.first_name, A.last_name, AL.title"
				+ "FROM song AS S INNER JOIN has AS H ON S.id = H.id_song"
				+ "INNER JOIN artist AS A ON A.id = H.id_artist" + "LEFT JOIN album AS AL ON AL.id = H.id_album"
				+ "INNER JOIN genre AS G ON G.id = S.id_genre" + "INNER JOIN artist AS AR ON AR.id = AL.id_artist;";

		PreparedStatement pstm = conn.prepareStatement(sql);

		ResultSet rs = pstm.executeQuery();
		List<Song> list = new ArrayList<Song>();
		ListIterator<Song> cursor = list.listIterator();
		String previous_title = "";

		while (rs.next()) {

			String title = rs.getString("S.title");
			String first_name_artist = rs.getString("first_name");
			String last_name_artist = rs.getString("last_name");
			String artist = first_name_artist + " " + last_name_artist;

			// add artist to previous song
			if (title == previous_title) {
				Song previous_song = cursor.previous();
				previous_song.addArtist(artist);
				cursor.next();
			}

			else {

				float duration = rs.getFloat("S.duration");
				String genre = rs.getString("descr");
				int year = rs.getInt("S.year");
				String album = rs.getString("AL.title");

				if (rs.wasNull()) { // if song is a single
					album = " ";
				}

				Song best_song = new Song();
				best_song.setTitle(title);
				best_song.setDuration(duration);
				best_song.setGenre(genre);
				best_song.addArtist(artist);
				best_song.setAlbum(album);
				best_song.setYear(year);
				best_song.setIs_top(true);

				list.add(best_song);
				cursor.next();
			}

			previous_title = title;
		}

		if (list.size() > 0) {
			return list;
		}

		return null;
	}

	// RETUNR ID_GENRE
	public static int Return_ID_genre(Connection conn, String genre) throws SQLException {
		String sql = "SELECT id FROM genre WHERE descr = ?";

		PreparedStatement pstm = conn.prepareStatement(sql);
		pstm.setString(1, genre);

		ResultSet rs = pstm.executeQuery();

		return rs.getInt("id");
	}

	// RETUNR ID_ARTIST
	public static int Return_ID_artist(Connection conn, String name, String surname) throws SQLException {
		String sql = "SELECT id FROM artist WHERE first_name = ?, last_name = ?";

		PreparedStatement pstm = conn.prepareStatement(sql);
		pstm.setString(1, name);
		pstm.setString(1, surname);

		ResultSet rs = pstm.executeQuery();

		return rs.getInt("id");
	}

	// ADD SONG (return the id generated by the dbb)
	public static int insertSong(Connection conn, Song song) throws SQLException {
		String sql = "Insert into Song values (?,?,?,?,?)";

		PreparedStatement pstm = conn.prepareStatement(sql);

		String title = song.getTitle();
		int year = song.getYear();
		Float duration = song.getDuration();
		String genre = song.getGenre();
		Boolean isTop = song.isIs_top();
		int id_genre = Return_ID_genre(conn, genre);

		pstm.setString(1, title);
		pstm.setInt(2, year);
		pstm.setFloat(3, duration);
		pstm.setBoolean(4, isTop);
		pstm.setInt(5, id_genre);

		pstm.executeUpdate();

		String sql1 = "SELECT id FROM song WHERE title=?, year=?,duration=?,rate_top=?,id_genre=?)";

		PreparedStatement pstm1 = conn.prepareStatement(sql1);

		pstm1.setString(1, title);
		pstm1.setInt(2, year);
		pstm1.setFloat(3, duration);
		pstm1.setBoolean(4, isTop);
		pstm1.setInt(5, id_genre);

		ResultSet rs = pstm1.executeQuery();

		int id = rs.getInt("id");

		return id;
	}

	// ADD ARTIST (return the id generated by the dbb)
	public static int insertArtist(Connection conn, String fullname) throws SQLException {
		String sql = "Insert into artist values (?)";

		PreparedStatement pstm = conn.prepareStatement(sql);

		pstm.setString(1, fullname);

		pstm.executeUpdate();

		String sql1 = "SELECT id FROM artist WHERE first_name=?, last_name=?";

		PreparedStatement pstm1 = conn.prepareStatement(sql1);

		pstm.setString(1, fullname);

		ResultSet rs = pstm1.executeQuery();

		int id = rs.getInt("id");

		return id;
	}

	// ADD ALBUM (return the id generated by the dbb)
	public static int insertAlbum(Connection conn, Album album, String firstname, String lastname) throws SQLException {
		String sql = "Insert into album values (?,?,?,?,?)";

		PreparedStatement pstm = conn.prepareStatement(sql);

		String title = album.getName();
		int year = album.getYear();
		Float duration = album.getDuration();
		Blob cover = album.getCover();
		int id_artist = Return_ID_artist(conn, firstname, lastname);

		pstm.setString(1, title);
		pstm.setInt(2, id_artist);
		pstm.setFloat(3, duration);
		pstm.setInt(4, year);
		pstm.setBlob(5, cover);

		pstm.executeUpdate();

		String sql1 = "SELECT id FROM album WHERE title = ?, id_artist=?, year=?";

		PreparedStatement pstm1 = conn.prepareStatement(sql1);

		pstm.setString(1, title);
		pstm.setInt(2, id_artist);
		pstm.setInt(3, year);

		ResultSet rs = pstm1.executeQuery();

		int id = rs.getInt("id");

		return id;
	}

	// ADD LINK ALBUM, ARTIST AND RELATIVE SONG
	public static void link(Connection conn, int id_album, int id_song, int id_artist) throws SQLException {
		String sql = "Insert into has values (?,?,?)";

		PreparedStatement pstm = conn.prepareStatement(sql);

		pstm.setInt(1, id_song);
		pstm.setInt(2, id_artist);
		pstm.setInt(3, id_album);

		pstm.executeUpdate();

	}

	// LIST ALBUMS
	public static List<Album> listAlbums(Connection conn) throws SQLException {
		String sql = "SELECT title, first_name, last_name, duration, year, cover"
				+ "FROM album AS AL INNER JOIN artist AS AR ON AL.id = AR.id;";

		PreparedStatement pstm = conn.prepareStatement(sql);

		ResultSet rs = pstm.executeQuery();
		List<Album> list = new ArrayList<Album>();

		while (rs.next()) {
			String title = rs.getString("title");
			float duration = rs.getFloat("duration");
			String first_name_artist = rs.getString("first_name");
			String last_name_artist = rs.getString("last_name");
			int year = rs.getInt("year");
			Blob cover = rs.getBlob("cover");
			String artist = first_name_artist + " " + last_name_artist;

			Album album = new Album();
			album.setName(title);
			album.setYear(year);
			album.setDuration(duration);
			album.setArtist(artist);
			album.setCover(cover);

			list.add(album);
		}

		if (list.size() > 0) {
			return list;
		}

		return null;
	}

	// list playlist per user
	public static List<Playlist> findPlaylists(Connection conn, String user) throws SQLException {
		String sql = "SELECT title "
				+ "FROM playlist AS P INNER JOIN listen_to AS L ON P.id = id_playlist INNER JOIN users AS U ON U.id = id_user;"
				+ "WHERE U.email = ?";

		PreparedStatement pstm = conn.prepareStatement(sql);
		pstm.setString(1, user);

		ResultSet rs = pstm.executeQuery();

		List<Playlist> list = new ArrayList<Playlist>();

		while (rs.next()) {

			String title = rs.getString("title");
			Playlist playlist = new Playlist(title);
			list.add(playlist);

		}

		if (list.size() > 0) {
			return list;
		}

		return null;
	}

	/* ADD PLAYLIST
	public static void add_playlist(Connection conn, Playlist playlist) throws SQLException {
		String sql = "Insert into playlist values (?, ?)";

		PreparedStatement pstm = conn.prepareStatement(sql);

		String title = playlist.getTitle();
		int id = playlist.getId();

		pstm.setString(2, title);
		pstm.setInt(1, id);

		pstm.executeUpdate();
	}


	public static void link_playlsit(Connection conn, int id_playlist, String email) throws SQLException {
		String sql = "SELECT id FROM users WHERE email = ?";

		PreparedStatement pstm = conn.prepareStatement(sql);
		pstm.setString(1, email);

		ResultSet rs = pstm.executeQuery();

		int id = rs.getInt("id");

		String sql1 = "Insert into listen_to values (?, ?)";

		PreparedStatement pstm1 = conn.prepareStatement(sql1);

		pstm1.setInt(2, id_playlist);
		pstm1.setInt(1, id);

		pstm1.executeUpdate();
	}
*/
	public static void deleteProduct(Connection conn, String code) throws SQLException {
		String sql = "Delete From Product where Code= ?";

		PreparedStatement pstm = conn.prepareStatement(sql);

		pstm.setString(1, code);

		pstm.executeUpdate();
	}

}
