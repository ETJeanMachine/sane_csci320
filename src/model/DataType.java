package model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public abstract class DataType {

    private final int id;
    private final ResultSet set;

    public DataType(ResultSet set) throws SQLException {
        this.set = set;
        this.id = set.getInt(1);
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

    public ResultSet getSet() {
        return this.set;
    }

    public int getID() {
        return this.id;
    }

}
