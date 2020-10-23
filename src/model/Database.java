package model;

import org.postgresql.util.PSQLException;

import java.sql.*;
import java.util.ArrayList;

/**
 * The class that connects us to the primary database of the system.
 *
 * @author Eric Jean
 */
public class Database {

    //
    // Attributes
    //

    // This is the object that connects us to our database.
    private final Connection connection;

    /**
     * This constructor establishes a connection with our primary database.
     */
    public Database(String password) throws Exception {
        Class.forName("org.postgresql.Driver");
        // These constants are used to parse a properties file that is securely stored on our computer, as to not
        // disturb unwanted access to the database.
        String username = "p320_14";
        String url = "jdbc:postgresql://reddwarf.cs.rit.edu:5432/" + username;
        this.connection = DriverManager.getConnection(url, username, password);
        connection.setAutoCommit(true);
    }

    public void closeConnection() throws SQLException {
        connection.close();
    }

    /**
     *
     * @param search
     * @return
     * @throws SQLException
     */
    public ArrayList<Song> searchForSongs(String search) throws SQLException {
        search = search.toLowerCase();
        ArrayList<Song> songs = new ArrayList<>();
        Statement stmt = connection.createStatement();
        ResultSet set = stmt.executeQuery("select song.* from song where lower(song.title) like '%" + search + "%';");
        while (set.next()) {
            Song song = new Song(set);
            getSongGenres(song);
            getSongArtists(song);
            getTotalPlayCount(song);
            songs.add(song);
        }
        stmt.close();
        set.close();
        return songs;
    }

    /**
     * This returns a list of albums based upon a search parameter specifying what album to look for.
     *
     * @param search The pattern we are looking for (an album name).
     * @return An arraylist of the albums matching that search entry.
     * @throws SQLException when there is an error in parsing the database.
     */
    public ArrayList<Album> searchForAlbums(String search) throws SQLException {
        search = search.toLowerCase();
        ArrayList<Album> albums = new ArrayList<>();
        Statement stmt = connection.createStatement();
        ResultSet set = stmt.executeQuery("select album.* from album where lower(album_name) like '%" + search + "%';");
        while (set.next()) {
            Album album = new Album(set);
            getSongsInAlbum(album);
            getAlbumGenres(album);
            albums.add(album);
        }
        set.close();
        stmt.close();
        return albums;
    }

    /**
     * @param id
     * @return
     * @throws SQLException
     */
    public User fetchUser(int id) throws SQLException {
        Statement stmt = connection.createStatement();
        ResultSet set = stmt.executeQuery("select * from users where user_id=" + id);
        set.next();
        User user;
        try {
            user = new User(set);
        } catch (PSQLException e) {
            stmt.executeUpdate("insert into users (user_id) values (" + id + ")");
            set = stmt.executeQuery("select * from users where user_id=" + id);
            set.next();
            user = new User(set);
        }
        getUserSongs(user);
        getUserAlbums(user);
        set.close();
        stmt.close();
        return user;
    }

    /**
     *
     * @param user
     * @param song
     * @throws SQLException
     */
    public void addSongToUserLibrary(User user, Song song) throws SQLException {
        if(user.getSongLibrary().contains(song)) {
            throw new SQLException("Song already in user library!");
        }
        String values = String.format("(%d, %d, %d);", user.getID(), song.getID(), 0);
        Statement stmt = connection.createStatement();
        stmt.executeUpdate("insert into owns_song (user_id, song_id, play_count) values " + values);
        song.setPlay_count(0);
        user.addSong(song);
        stmt.close();
    }

    // TODO
    public void addAlbumToUserLibrary(User user, Album album) throws SQLException {
        if(user.getAlbumLibrary().contains(album)) {
            throw new SQLException("Album already in user library!");
        }
        String values = String.format("(%d, %d);", user.getID(), album.getID());
        Statement stmt = connection.createStatement();
        stmt.executeUpdate("insert into owns_album (user_id, album_id) values " + values);
        user.addAlbum(album);
        // Add songs in an album to the user library.
        for(Song s : album.getSongs()) {
            try {
                addSongToUserLibrary(user, s);
            } catch (SQLException ignored) {}
        }
        stmt.close();
    }

    /**
     *
     * @param user
     * @param song
     * @throws SQLException
     */
    public void playSong(User user, Song song) throws SQLException {
        if(!user.getSongLibrary().contains(song)) {
            throw new SQLException("Song not in user library!");
        }
        Timestamp now = new Timestamp(System.currentTimeMillis());
        song.playSong(now);
        String update = String.format("play_count=play_count + 1, time_stamp='%s'", song.getTime_stamp());
        String condition = String.format("song_id=%d and user_id=%d", song.getID(), user.getID());
        Statement stmt = connection.createStatement();
        stmt.executeUpdate("update owns_song set " + update + " where " + condition);
        stmt.close();
    }

    /**
     *
     * @param album
     * @throws SQLException
     */
    private void getSongsInAlbum(Album album) throws SQLException {
        int id = album.getID();
        Statement stmt = connection.createStatement();
        ResultSet set = stmt.executeQuery("select song.*, has.track_number from song, has where has.album_id=" + id +
                " and has.song_id=song.song_id order by track_number");
        while (set.next()) {
            Song song = new Song(set);
            song.setTrack_number(set.getInt("track_number"));
            getSongGenres(song);
            getSongArtists(song);
            getTotalPlayCount(song);
            album.addSong(song);
        }
        set.close();
        stmt.close();
    }

    /**
     *
     * @param song
     * @throws SQLException
     */
    private void getTotalPlayCount(Song song) throws SQLException {
        int id = song.getID();
        Statement stmt = connection.createStatement();
        ResultSet set = stmt.executeQuery("select play_count from owns_song where song_id=" + id);
        int total_count = 0;
        while (set.next()) {
            total_count += set.getInt("play_count");
        }
        song.setPlay_count(total_count);
        stmt.close();
        set.close();
    }

    /**
     *
     * @param song
     * @throws SQLException
     */
    private void getSongArtists(Song song) throws SQLException {
        int id = song.getID();
        Statement stmt = connection.createStatement();
        ResultSet set = stmt.executeQuery("select artist.* from artist, created_by where created_by.artist_id=artist.artist_id " +
                "and created_by.song_id=" + id);
        while (set.next()) {
            song.addArtist(new Artist(set));
        }
        stmt.close();
        set.close();
    }

    /**
     *
     * @param song
     * @throws SQLException
     */
    private void getSongGenres(Song song) throws SQLException {
        int song_id = song.getID();
        Statement stmt = connection.createStatement();
        ResultSet set = stmt.executeQuery("select genre_type from has_genre_song where song_id=" + song_id);
        while(set.next()) {
            song.addGenre(set.getString("genre_type"));
        }
        stmt.close();
        set.close();
    }

    // TODO
    private void getAlbumGenres(Album album) throws SQLException {
        int album_id = album.getID();
        Statement stmt = connection.createStatement();
        ResultSet set = stmt.executeQuery("select genre_type from has_genre_album where album_id=" + album_id);
        while(set.next()) {
            album.addGenre(set.getString("genre_type"));
        }
        stmt.close();
        set.close();
    }

    /**
     *
     * @param user
     * @throws SQLException
     */
    private void getUserSongs(User user) throws SQLException {
        int user_id = user.getID();
        Statement stmt = connection.createStatement();
        ResultSet set = stmt.executeQuery("select song.*, owns_song.play_count, owns_song.time_stamp from song inner join " +
                "owns_song on song.song_id=owns_song.song_id and owns_song.user_id=" + user_id + ";");
        while(set.next()) {
            Song song = new Song(set);
            getSongArtists(song);
            getSongGenres(song);
            song.setTime_stamp(set.getTimestamp("time_stamp"));
            song.setPlay_count(set.getInt("play_count"));
            user.addSong(song);
        }
        stmt.close();
        set.close();
    }

    /**
     *
     * @param user
     * @throws SQLException
     */
    private void getUserAlbums(User user) throws SQLException {
        int user_id = user.getID();
        Statement stmt = connection.createStatement();
        ResultSet set = stmt.executeQuery("select album.* from album inner join owns_album on " +
                "album.album_id=owns_album.album_id and owns_album.user_id=" + user_id + ";");
        while(set.next()) {
            Album album = new Album(set);
            getAlbumGenres(album);
            getSongsInAlbum(album);
            user.addAlbum(album);
        }
        stmt.close();
        set.close();
    }

}
