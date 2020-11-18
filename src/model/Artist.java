package model;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Artist extends DataType {

    //
    // Attributes
    //

    private final String artist_name;
    private final Date dob;

    /**
     * Constructor for the Artist datatype
     *
     * @param set a result set containing the artist data.
     * @throws SQLException if there is an error in parsing data.
     */
    public Artist(ResultSet set) throws SQLException {
        super(set);
        this.artist_name = set.getString("artist_name");
        this.dob = set.getDate("dob");
    }

    /**
     * Gets the name of the artist.
     *
     * @return the name of the artist.
     */
    public String getArtist_name() {
        return artist_name;
    }

    /**
     * Gets the date the artist was born
     *
     * @return a date of the artist's birth.
     */
    public Date getDob() {
        return dob;
    }
}
