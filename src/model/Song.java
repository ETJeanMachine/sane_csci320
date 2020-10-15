package model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Song {

    private final int song_id;
    private final String title;
    private final int length;
    private final int play_count;
    private final ArrayList<String> genres = new ArrayList<>();
    private int album_id;
    private int artist_id;

    /**
     * Builds a song object using a set containing all the database items for a song.
     * @param set The set that the song is contained within.
     * @throws SQLException If there is an error in the parsing of the SQL.
     */
    public Song(ResultSet set) throws SQLException {
        // Setting the info of the song.
        this.song_id = set.getInt("song_id");
        this.title = set.getString("title");
        this.length = set.getInt("length");
        this.play_count = set.getInt("play_count");
    }

    public ArrayList<String> getGenres() {
        return this.genres;
    }

    public int getArtist_id() {
        return  this.album_id;
    }

    public int getSong_id() {
        return  this.song_id;
    }

    @Override
    public String toString() {
        return String.format("{song_id: %d, title: %s, length: %s, play_count: %d}", song_id, title, length, play_count);
    }
}
