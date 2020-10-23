package model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;

public class Song extends DataType {

    private final String title;
    private final int length;
    private final ArrayList<String> genres = new ArrayList<>();
    private final ArrayList<Artist> artists = new ArrayList<>();

    private int track_number = 0;
    private int play_count = 0;
    private Timestamp time_stamp = null;

    /**
     * Builds a song object using a set containing all the database items for a song.
     * @param set The set that the song is contained within.
     * @throws SQLException If there is an error in the parsing of the SQL.
     */
    public Song(ResultSet set) throws SQLException {
        super(set);
        this.title = set.getString("title");
        this.length = set.getInt("length");
    }

    public void setTrack_number(int i) {
        this.track_number = i;
    }

    public void addGenre(String genre) {
        genres.add(genre);
    }

    public void addArtist(Artist artist) {
        this.artists.add(artist);
    }

    public void playSong(Timestamp time) {
        play_count++;
        this.time_stamp = time;
    }

    public void setPlay_count(int play_count) {
        this.play_count = play_count;
    }

    public void setTime_stamp(Timestamp time) {
        this.time_stamp = time;
    }

    public String getLength() {
        int min = length / 60;
        int sec = length % 60;
        return String.format("%d:%02d", min, sec);
    }

    public String getTitle() {
        return this.title;
    }

    public String getGenres() {
        return Arrays.toString(genres.toArray()).replaceAll("[\\[\\]]", "");
    }

    public String getArtists() {
        ArrayList<String> artistNames = new ArrayList<>();
        for(Artist a : artists) {
            artistNames.add(a.getArtist_name());
        }
        return Arrays.toString(artistNames.toArray()).replaceAll("[\\[\\]]", "");
    }

    public ArrayList<Artist> getArtistList() {
        return this.artists;
    }

    public ArrayList<String> getGenreList() {
        return this.genres;
    }

    public int getTrack_number() {
        return this.track_number;
    }

    public int getPlay_count() {
        return play_count;
    }

    public Timestamp getTime_stamp() {
        return this.time_stamp;
    }

    @Override
    public String toString() {
        return String.format("{song_id: %d, title: %s, length: %s}", getID(), title, length);
    }
}
