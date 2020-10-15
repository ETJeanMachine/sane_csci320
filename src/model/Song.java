package model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Song {

    private final int song_id;
    private String title;
    private int length;
    private int play_count;
    private ArrayList<Genre> genres;
    private ArrayList<Album> albums;
    private ArrayList<Artist> artist;

    public Song(int songID, Statement statement) throws SQLException {
        this.song_id = songID;
        getSongInfo(statement);
    }

    private void getSongInfo(Statement statement) throws SQLException {
        String sql = String.format("select * from song where song_id=%d", song_id);
        ResultSet set = statement.executeQuery(sql);
        set.next();
        this.title = set.getString("title");
        this.length = set.getInt("length");
        this.play_count = set.getInt("play_count");
    }

    public int getSong_id() {
        return song_id;
    }

    public String toString() {
        return String.format("{song_id: %d, title: %s, length: %s, play_count: %d}", song_id, title, length, play_count);
    }
}
