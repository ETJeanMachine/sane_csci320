package model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

public class Song extends DataType {

    //
    // Attributes
    //

    private final String title;
    private final int length;
    private final ArrayList<String> genres = new ArrayList<>();
    private final ArrayList<Artist> artists = new ArrayList<>();
    private int track_number = 0;
    private int play_count = 0;

    /**
     * Builds a song object using a set containing all the database items for a song.
     *
     * @param set The set that the song is contained within.
     * @throws SQLException If there is an error in the parsing of the SQL.
     */
    public Song(ResultSet set) throws SQLException {
        super(set);
        this.title = set.getString("title");
        this.length = set.getInt("length");
    }

    /**
     * Adds a genre to a song.
     *
     * @param genre the genre we are adding.
     */
    public void addGenre(String genre) {
        genres.add(genre);
    }

    /**
     * Adds an artist to a song.
     *
     * @param artist the artist we are adding.
     */
    public void addArtist(Artist artist) {
        this.artists.add(artist);
    }

    /**
     * Increments a songs play count.
     */
    public void playSong() {
        play_count++;
    }

    /**
     * Gets the song's length as a string.
     *
     * @return a string format of the song's length.
     */
    public String getLength() {
        return formatLength(length);
    }

    /**
     * Get's the songs title as a string.
     *
     * @return the title of the song.
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Returns the list of genres as a formatted string.
     *
     * @return a formatted string of genres.
     */
    public String getGenres() {
        return Arrays.toString(genres.toArray()).replaceAll("[\\[\\]]", "");
    }

    /**
     * Returns the list of artists as a formatted string.
     *
     * @return a formatted string of artists.
     */
    public String getArtists() {
        ArrayList<String> artistNames = new ArrayList<>();
        for (Artist a : artists) {
            artistNames.add(a.getArtist_name());
        }
        return Arrays.toString(artistNames.toArray()).replaceAll("[\\[\\]]", "");
    }

    /**
     * Gets the track number as an int.
     *
     * @return the track number.
     */
    public int getTrack_number() {
        return this.track_number;
    }

    /**
     * Sets the track number of a song in an album.
     *
     * @param i the number we are setting it to.
     */
    public void setTrack_number(int i) {
        this.track_number = i;
    }

    /**
     * Gets the play count as an int.
     *
     * @return the play count.
     */
    public int getPlay_count() {
        return play_count;
    }

    /**
     * Sets a songs play count
     *
     * @param play_count the number we are setting it to
     */
    public void setPlay_count(int play_count) {
        this.play_count = play_count;
    }

    /**
     * Returns an arraylist of the artists who made a song.
     *
     * @return an arraylist of artists.
     */
    public ArrayList<Artist> getArtistList() {
        return this.artists;
    }

    @Override
    public String toString() {
        return String.format("{song_id: %d, title: %s, length: %s}", getID(), title, length);
    }
}
