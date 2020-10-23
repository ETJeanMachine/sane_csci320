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
        // These constants are used to parse a properties file that is securely stored on our computer, as to not disturb unwanted access to the database.
        String username = "p320_14";
        String url = "jdbc:postgresql://reddwarf.cs.rit.edu:5432/" + username;
        this.connection = DriverManager.getConnection(url, username, password);
        connection.setAutoCommit(true);
    }

    public void closeConnection() throws SQLException {
        connection.close();
    }

    public ArrayList<Song> searchForSongs(String search) throws SQLException {
        search = search.toLowerCase();
        ArrayList<Song> songs = new ArrayList<>();
        Statement stmt = connection.createStatement();
        ResultSet set = stmt.executeQuery("select song.*, has_genre_song.genre_type from song, has_genre_song where " +
                "lower(song.title) like '%" + search + "%' and has_genre_song.song_id=song.song_id;");
        while (set.next()) {
            Song song = new Song(set);
            song.addGenre(set.getString("genre_type"));
            setSongArtists(song);
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
        ResultSet set = stmt.executeQuery("select album.*, has_genre_album.genre_type from album, has_genre_album " +
                "where lower(album_name) like '%" + search + "%' and album.album_id=has_genre_album.album_id;");
        while (set.next()) {
            albums.add(new Album(set));
        }
        set.close();
        stmt.close();
        return albums;
    }

    public ArrayList<Artist> searchForArtist(String search) throws SQLException {
        search = search.toLowerCase();
        ArrayList<Artist> artists = new ArrayList<>();
        Statement stmt = connection.createStatement();
        ResultSet set = stmt.executeQuery("select * from artist where lower(artist_name) like '%" + search + "%';");
        while (set.next()) {
            artists.add(new Artist(set));
        }
        set.close();
        stmt.close();
        return artists;
    }

    public ArrayList<Song> getSongsInAlbum(Album album) throws SQLException {
        int id = album.getID();
        ArrayList<Song> songs = new ArrayList<>();
        Statement stmt = connection.createStatement();
        ResultSet set = stmt.executeQuery("select song.*, has.track_number from song, has where has.album_id=" + id +
                " and has.song_id=song.song_id order by track_number");
        while (set.next()) {
            Song song = new Song(set);
            song.setTrack_number(set.getInt("track_number"));
            setSongArtists(song);
            songs.add(song);
        }
        set.close();
        stmt.close();
        return songs;
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
        set.close();
        stmt.close();
        return user;
    }

    private void setTotalPlayCount(Song song) {
        int id = song.getID();
    }

    private void setSongArtists(Song song) throws SQLException {
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

}
