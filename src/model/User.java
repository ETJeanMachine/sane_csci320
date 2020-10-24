package model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class User extends DataType {

    //
    // Attributes
    //

    private final ArrayList<Song> songLibrary = new ArrayList<>();
    private final ArrayList<Album> albumLibrary = new ArrayList<>();
    private final ArrayList<Artist> artistLibrary = new ArrayList<>();

    /**
     * This constructs a user given off a SQL query result.
     *
     * @param set the result of the SQL query.
     * @throws SQLException if there is an error in parsing the user.
     */
    public User(ResultSet set) throws SQLException {
        super(set);
    }

    /**
     * This adds an song to our library.
     *
     * @param song the song we are adding.
     */
    public void addSong(Song song) {
        songLibrary.add(song);
        addArtists(song.getArtistList());
    }

    /**
     * This adds an album to our library.
     *
     * @param album the album we are adding.
     */
    public void addAlbum(Album album) {
        albumLibrary.add(album);
        addArtists(album.getArtistList());
    }

    /**
     * This adds artists to our artist library.
     *
     * @param artists an arraylist of artists.
     */
    private void addArtists(ArrayList<Artist> artists) {
        for (Artist a : artists) {
            if (!artistLibrary.contains(a)) {
                artistLibrary.add(a);
            }
        }
    }

    /**
     * The returns the song library.
     *
     * @return an arraylist of songs that the user owns.
     */
    public ArrayList<Song> getSongLibrary() {
        return this.songLibrary;
    }

    /**
     * The returns the album library.
     *
     * @return an arraylist of albums that the user owns.
     */
    public ArrayList<Album> getAlbumLibrary() {
        return this.albumLibrary;
    }

    /**
     * The returns the artist library.
     *
     * @return an arraylist of artists that the user has songs/albums made by.
     */
    public ArrayList<Artist> getArtistLibrary() {
        return this.artistLibrary;
    }

    public String toString() {
        return String.format("{user_id: %d}", getID());
    }

}
