package model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public abstract class DataType {

    //
    // Attributes
    //

    private final int id;

    /**
     * This constructs a generic datatype.
     *
     * @param set a SQL entry set containing an ID.
     * @throws SQLException if this entry has no ID inside of it.
     */
    public DataType(ResultSet set) throws SQLException {
        this.id = set.getInt(1);
    }

    public static String formatLength(int length) {
        int min = length / 60;
        int sec = length % 60;
        return String.format("%d:%02d", min, sec);
    }

    /**
     * Returns the ID of the datatype.
     *
     * @return the int id of the data.
     */
    public int getID() {
        return this.id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataType dataType = (DataType) o;
        return id == dataType.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("{id: %d}", id);
    }
}
