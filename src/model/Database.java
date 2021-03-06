package model;

import javafx.util.Pair;
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
     * This class connects a client to the database.
     *
     * @param password the password of the database.
     * @throws Exception if there is an error in connection.
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

    /**
     * CLoses the database connection.
     *
     * @throws SQLException if there is an error in closure.
     */
    public void closeConnection() throws SQLException {
        connection.close();
    }

    /**
     * Searches for songs within in a database.
     *
     * @param search the search term we are looking for.
     * @return an arraylist of songs that match our term.
     * @throws SQLException if there is an error in parsing data.
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
     * Fetches a user with a given ID.
     *
     * @param id the id we are looking for.
     * @return the user with said ID.
     * @throws SQLException if there is an error in parsing data.
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
     * Gets the times that a given user has played a song.
     *
     * @param user the user we are looking at.
     * @param song the song we are looking at.
     * @return an arraylist of times a user has played a given song.
     * @throws SQLException if there is an error in parsing data.
     */
    public ArrayList<Timestamp> getTimeStamps(User user, Song song) throws SQLException {
        ArrayList<Timestamp> timestamps = new ArrayList<>();
        Statement stmt = connection.createStatement();
        String condition = String.format("user_id=%d and song_id=%d", user.getID(), song.getID());
        ResultSet set = stmt.executeQuery("select time_stamp from time_records where " + condition);
        while (set.next()) {
            timestamps.add(set.getTimestamp(1));
        }
        return timestamps;
    }

    /**
     * Adds a song to the users library.
     *
     * @param user the user we are updating the library of.
     * @param song the song we are adding.
     * @throws SQLException if there is an error in parsing data.
     */
    public void addSongToUserLibrary(User user, Song song) throws SQLException {
        if (user.getSongLibrary().contains(song)) {
            throw new SQLException("Song already in user library!");
        }
        String values = String.format("(%d, %d);", user.getID(), song.getID());
        Statement stmt = connection.createStatement();
        stmt.executeUpdate("insert into owns_song (user_id, song_id) values " + values);
        song.setPlay_count(0);
        user.addSong(song);
        stmt.close();
    }

    /**
     * Adds an album to the users library.
     *
     * @param user  the user we are updating the library of.
     * @param album the album we are adding.
     * @throws SQLException if there is an error in parsing data.
     */
    public void addAlbumToUserLibrary(User user, Album album) throws SQLException {
        if (user.getAlbumLibrary().contains(album)) {
            throw new SQLException("Album already in user library!");
        }
        String values = String.format("(%d, %d);", user.getID(), album.getID());
        Statement stmt = connection.createStatement();
        stmt.executeUpdate("insert into owns_album (user_id, album_id) values " + values);
        user.addAlbum(album);
        // Add songs in an album to the user library.
        for (Song s : album.getSongs()) {
            try {
                addSongToUserLibrary(user, s);
            } catch (SQLException ignored) {
            }
        }
        stmt.close();
    }

    /**
     * "Plays" a song, or increments it's play_count and inserts into time_records the time it was "played" at.
     *
     * @param user The user playing the song
     * @param song The song being played
     * @throws SQLException if there is an error in parsing data.
     */
    public void playSong(User user, Song song) throws SQLException {
        if (!user.getSongLibrary().contains(song)) {
            throw new SQLException("Song not in user library!");
        }
        // String condition = String.format("play_count=play_count + 1 where song_id=%d and user_id=%d;", song.getID(), user.getID());
        Statement stmt = connection.createStatement();
        String values = String.format("(localtimestamp, %d, %d);", song.getID(), user.getID());
        stmt.executeUpdate("update owns_song set play_count=play_count+1 where user_id=" + user.getID() + " and song_id=" + song.getID());
        stmt.executeUpdate("insert into time_records (time_stamp, song_id, user_id) values " + values);
        stmt.close();
        song.playSong();
    }

    /**
     * Sets an album DataType to have it's songs stored within.
     *
     * @param album the album we are setting up.
     * @throws SQLException if there is an error in getting the songs for an album.
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
     * Gets the total amount of times a song has been played.
     *
     * @param song the song we are looking at.
     * @throws SQLException if there is an error in parsing data.
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
     * Adds artists to a given song.
     *
     * @param song the song we are adding artists to.
     * @throws SQLException if there is an error in parsing data.
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
     * Adds genres to a song data type.
     *
     * @param song the song we are finding the genres for.
     * @throws SQLException if there is an error in parsing data.
     */
    private void getSongGenres(Song song) throws SQLException {
        int song_id = song.getID();
        Statement stmt = connection.createStatement();
        ResultSet set = stmt.executeQuery("select genre_type from has_genre_song where song_id=" + song_id);
        while (set.next()) {
            song.addGenre(set.getString("genre_type"));
        }
        stmt.close();
        set.close();
    }

    /**
     * Adds genres to an album data type.
     *
     * @param album the album we are finding the genres for.
     * @throws SQLException if there is an error in parsing data.
     */
    private void getAlbumGenres(Album album) throws SQLException {
        int album_id = album.getID();
        Statement stmt = connection.createStatement();
        ResultSet set = stmt.executeQuery("select genre_type from has_genre_album where album_id=" + album_id);
        while (set.next()) {
            album.addGenre(set.getString("genre_type"));
        }
        stmt.close();
        set.close();
    }

    /**
     * Sets up the song library for a given user.
     *
     * @param user the user we are setting up the album library for.
     * @throws SQLException if there is an error in setting up the library.
     */
    private void getUserSongs(User user) throws SQLException {
        int user_id = user.getID();
        Statement stmt = connection.createStatement();
        ResultSet set = stmt.executeQuery("select song.*, owns_song.play_count from song inner join " +
                "owns_song on song.song_id=owns_song.song_id and owns_song.user_id=" + user_id + ";");
        while (set.next()) {
            Song song = new Song(set);
            getSongArtists(song);
            getSongGenres(song);
            song.setPlay_count(set.getInt("play_count"));
            user.addSong(song);
        }
        stmt.close();
        set.close();
    }

    /**
     * Sets the album library for a given user.
     *
     * @param user the user we are setting up the album library for.
     * @throws SQLException if there is an error in setting up the library.
     */
    private void getUserAlbums(User user) throws SQLException {
        int user_id = user.getID();
        Statement stmt = connection.createStatement();
        ResultSet set = stmt.executeQuery("select album.* from album inner join owns_album on " +
                "album.album_id=owns_album.album_id and owns_album.user_id=" + user_id + ";");
        while (set.next()) {
            Album album = new Album(set);
            getAlbumGenres(album);
            getSongsInAlbum(album);
            user.addAlbum(album);
        }
        stmt.close();
        set.close();
    }

    //
    // Analytical Queries
    //

    /**
     * Returns the mean album length for a user.
     *
     * @param user the user we are looking at.
     * @return a string of the length of an album.
     * @throws SQLException if there is an error in parsing data.
     */
    public String meanAlbumLength(User user) throws SQLException {
        Statement stmt = connection.createStatement();
        ResultSet set = stmt.executeQuery("select avg(sum_len) from (select sum(song.length) as sum_len from " +
                "song, users, owns_album, has, album where users.user_id = " + user.getID() + " and users.user_id = " +
                "owns_album.user_id and owns_album.album_id = album.album_id and album.album_id = has.album_id and " +
                "has.song_id = song.song_id group by album.album_id) as abc");
        set.next();
        return DataType.formatLength(set.getInt(1));
    }

    /**
     * Finds the mean length of individual songs in a users library.
     *
     * @param user the user we are looking at.
     * @return an string of the length of the song.
     * @throws SQLException if there is an error in parsing data.
     */
    public String meanSongLength(User user) throws SQLException {
        Statement stmt = connection.createStatement();
        ResultSet set = stmt.executeQuery("select ceil(avg(song.length)) as avg_len from users, " +
                "owns_song, song where users.user_id = owns_song.user_id and owns_song.song_id = song.song_id and " +
                "users.user_id = " + user.getID());
        set.next();
        return DataType.formatLength(set.getInt(1));
    }

    /**
     * Gets the five most owned genres by a user.
     *
     * @param user the user we are looking at.
     * @return an arraylist of length 5 (or less) of tuples -> containing both the genre and the amount of songs a user
     * owns in said genre.
     * @throws SQLException if there is an error in parsing data.
     */
    public ArrayList<Pair<String, Integer>> mostOwnedGenres(User user) throws SQLException {
        Statement stmt = connection.createStatement();
        ResultSet set = stmt.executeQuery("select has_genre_song.genre_type, count(has_genre_song.genre_type) from " +
                "users, owns_song, song, has_genre_song where users.user_id = owns_song.user_id and owns_song.song_id = " +
                "song.song_id and song.song_id = has_genre_song.song_id and users.user_id = " + user.getID() + " group by " +
                "has_genre_song.genre_type order by count(has_genre_song.genre_type) desc limit 5");
        ArrayList<Pair<String, Integer>> genres = new ArrayList<>();
        while (set.next()) {
            genres.add(new Pair<>(set.getString(1), set.getInt(2)));
        }
        return genres;
    }

    /**
     * Gets a user's 5 most owned artists.
     *
     * @param user the user we are looking at.
     * @return an arraylist of length 5 or less containing a pair of both the artist's name and the number of owned songs
     * by an artist.
     * @throws SQLException if there is an error in parsing data.
     */
    public ArrayList<Pair<String, Integer>> mostOwnedArtists(User user) throws SQLException {
        Statement stmt = connection.createStatement();
        ResultSet set = stmt.executeQuery("select artist.artist_name, count(artist.artist_id) from users, owns_song," +
                " song, created_by, artist where users.user_id = owns_song.user_id and owns_song.song_id = song.song_id " +
                "and song.song_id = created_by.song_id and created_by.artist_id = artist.artist_id and users.user_id = " +
                user.getID() + " group by artist.artist_name order  by count(artist.artist_id) desc limit 5");
        ArrayList<Pair<String, Integer>> artists = new ArrayList<>();
        while (set.next()) {
            artists.add(new Pair<>(set.getString(1), set.getInt(2)));
        }
        return artists;
    }

    /**
     * Gets a users top 5 most played songs.
     *
     * @param user the user we are looking at.
     * @return an arraylist of songs and the count of the total song plays by a user.
     * @throws SQLException if there is an error in parsing the data.
     */
    public ArrayList<Pair<String, Integer>> mostPlayedSongs(User user) throws SQLException {
        Statement stmt = connection.createStatement();
        ResultSet set = stmt.executeQuery("select song.title, owns_song.play_count from users, owns_song, song where " +
                "users.user_id = owns_song.user_id and owns_song.song_id = song.song_id and users.user_id = " + user.getID() +
                " order by owns_song.play_count desc limit 5");
        ArrayList<Pair<String, Integer>> songs = new ArrayList<>();
        while (set.next()) {
            songs.add(new Pair<>(set.getString(1), set.getInt(2)));
        }
        return songs;
    }

    /**
     * Gets a users top 5 most played genres.
     *
     * @param user the user we are looking at.
     * @return an arraylist of genres and the count of the total plays in said genre by a user.
     * @throws SQLException if there is an error in parsing data.
     */
    public ArrayList<Pair<String, Integer>> mostPlayedGenres(User user) throws SQLException {
        Statement stmt = connection.createStatement();
        ResultSet set = stmt.executeQuery("select has_genre_song.genre_type, count(has_genre_song.genre_type) from " +
                "users, owns_song, song, has_genre_song where users.user_id = owns_song.user_id and owns_song.song_id = " +
                "song.song_id and song.song_id = has_genre_song.song_id and users.user_id = " + user.getID() + " and " +
                "owns_song.play_count > 0 group by has_genre_song.genre_type order by count(has_genre_song.genre_type) " +
                "desc limit 5");
        ArrayList<Pair<String, Integer>> genres = new ArrayList<>();
        while (set.next()) {
            genres.add(new Pair<>(set.getString(1), set.getInt(2)));
        }
        return genres;
    }

    /**
     * Gets the total genre ownership for the entire database.
     *
     * @return An arraylist of genres and their ownership counts.
     * @throws SQLException if there is an error in parsing the data.
     */
    public ArrayList<Pair<String, Integer>> totalGenreOwnership() throws SQLException {
        Statement stmt = connection.createStatement();
        ResultSet set = stmt.executeQuery("select has_genre_song.genre_type, count(has_genre_song.genre_type) from " +
                "users, owns_song, song, has_genre_song where users.user_id = owns_song.user_id and owns_song.song_id " +
                "= song.song_id and song.song_id = has_genre_song.song_id group by has_genre_song.genre_type order by " +
                "count(has_genre_song.genre_type) desc");
        ArrayList<Pair<String, Integer>> genres = new ArrayList<>();
        while (set.next()) {
            genres.add(new Pair<>(set.getString(1), set.getInt(2)));
        }
        return genres;
    }

    /**
     * Gets a list of 10 songs recommended to play for a user.
     *
     * @param user the user we are recommending songs to.
     * @return An arraylist of songs from a users most played genre that they do not currently own.
     * @throws SQLException if there is an error in parsing data.
     */
    public ArrayList<String> recommendedSongs(User user) throws SQLException {
        Statement stmt = connection.createStatement();
        ResultSet set = stmt.executeQuery("select title from song where song.song_id in (select " +
                "has_genre_song.song_id from has_genre_song where has_genre_song.genre_type = (select " +
                "has_genre_song.genre_type from users, owns_song, song, has_genre_song where users.user_id = " +
                "owns_song.user_id and owns_song.song_id = song.song_id and song.song_id = has_genre_song.song_id " +
                "and users.user_id = " + user.getID() + " group by has_genre_song.genre_type order by " +
                "count(has_genre_song.genre_type) desc limit 1)) and song.song_id not in (select owns_song.song_id from " +
                "owns_song where owns_song.user_id = " + user.getID() + ") limit 10");
        ArrayList<String> songs = new ArrayList<>();
        while (set.next()) {
            songs.add(set.getString(1));
        }
        return songs;
    }

}
