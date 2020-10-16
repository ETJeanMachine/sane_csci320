package model;

import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class DataType {

    private final int id;

    public DataType(ResultSet set) throws SQLException {
        this.id = set.getInt(1);
    }

    public int getID() {
        return this.id;
    }

}
