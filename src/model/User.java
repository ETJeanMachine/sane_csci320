package model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class User extends DataType {

    private final ArrayList<Song> songLibrary = new ArrayList<>();
    private final ArrayList<Album> albumLibrary = new ArrayList<>();

    public User(ResultSet set) throws SQLException {
        super(set);
    }

    public ArrayList<Song> getSongLibrary() {
        return this.songLibrary;
    }

    public ArrayList<Album> getAlbumLibrary() {
        return this.albumLibrary;
    }

    public String toString() {
        return String.format("{user_id: %d}", getID());
    }

}
