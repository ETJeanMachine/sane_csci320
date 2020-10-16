package model;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Artist extends DataType {

    private final String artist_name;
    private final Date dob;

    public Artist(ResultSet set) throws SQLException {
        super(set);
        this.artist_name = set.getString("artist_name");
        this.dob = set.getDate("dob");
    }

    public String getArtist_name() {
        return artist_name;
    }

    public Date getDob() {
        return dob;
    }

}
