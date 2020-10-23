package model;

import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class DataType {

    private final int id;
    private final ResultSet set;

    public DataType(ResultSet set) throws SQLException {
        this.set = set;
        this.id = set.getInt(1);
    }

    public ResultSet getSet() {
        return this.set;
    }

    public int getID() {
        return this.id;
    }

}
