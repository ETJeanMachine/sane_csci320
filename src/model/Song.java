package model;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Song extends DataType {

    private final String title;
    private final int length;
    private final int play_count;

    /**
     * Builds a song object using a set containing all the database items for a song.
     * @param set The set that the song is contained within.
     * @throws SQLException If there is an error in the parsing of the SQL.
     */
    public Song(ResultSet set) throws SQLException {
        super(set);
        this.title = set.getString("title");
        this.length = set.getInt("length");
        this.play_count = set.getInt("play_count");
    }

    public int getPlay_count() {
        return this.play_count;
    }

    public int getLength() {
        return this.length;
    }

    public String getTitle() {
        return this.title;
    }

    @Override
    public String toString() {
        return String.format("{song_id: %d, title: %s, length: %s, play_count: %d}", getID(), title, length, play_count);
    }
}
