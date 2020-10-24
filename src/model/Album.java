package model;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

public class Album extends DataType {

    //
    // Attributes
    //

    private final Date release_date;
    private final String album_name;
    private final ArrayList<Song> songs = new ArrayList<>();
    private final ArrayList<Artist> artists = new ArrayList<>();
    private final ArrayList<String> genres = new ArrayList<>();

    /**
     * Constructor for an album.
     *
     * @param set the SQL set of an album.
     * @throws SQLException if there is an error in parsing data.
     */
    public Album(ResultSet set) throws SQLException {
        super(set);
        this.release_date = set.getDate("release_date");
        this.album_name = set.getString("album_name");
    }

    /**
     * Get the release date of an album.
     *
     * @return the album's release date.
     */
    public Date getRelease_date() {
        return release_date;
    }

    /**
     * Get the name of an album.
     *
     * @return the album's name.
     */
    public String getAlbum_name() {
        return album_name;
    }

    /**
     * Add a known genre for this album
     *
     * @param genre the genre we are adding.
     */
    public void addGenre(String genre) {
        genres.add(genre);
    }

    /**
     * Add a song to this album.
     *
     * @param song the song we are adding.
     */
    public void addSong(Song song) {
        songs.add(song);
        for (Artist a : song.getArtistList()) {
            if (!artists.contains(a)) {
                artists.add(a);
            }
        }
    }

    /**
     * Get the list of songs within the album.
     *
     * @return an arraylist of songs.
     */
    public ArrayList<Song> getSongs() {
        return this.songs;
    }

    /**
     * The list of artists who made this album.
     *
     * @return an arraylist of artists.
     */
    public ArrayList<Artist> getArtistList() {
        return artists;
    }

    /**
     * Get the artists who made this album.
     *
     * @return a string of artist names.
     */
    public String getArtists() {
        ArrayList<String> artistNames = new ArrayList<>();
        for (Artist a : artists) {
            artistNames.add(a.getArtist_name());
        }
        return Arrays.toString(artistNames.toArray()).replaceAll("[\\[\\]]", "");
    }

    /**
     * The genres within an album.
     *
     * @return a string of genres.
     */
    public String getGenres() {
        return Arrays.toString(genres.toArray()).replaceAll("[\\[\\]]", "");
    }
}
