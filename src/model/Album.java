package model;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Album extends DataType {

    private final Date release_date;
    private final String album_name;

    public Album(ResultSet set) throws SQLException {
        super(set);
        this.release_date = set.getDate("release_date");
        this.album_name = set.getString("album_name");
    }

    public Date getRelease_date() {
        return release_date;
    }

    public String getAlbum_name() {
        return album_name;
    }
}
