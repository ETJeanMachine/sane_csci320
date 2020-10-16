package model;

import java.sql.ResultSet;
import java.sql.SQLException;

public class User extends DataType {

    public User(ResultSet set) throws SQLException {
        super(set);
    }

    public String toString() {
        return String.format("{user_id: %d}", getID());
    }

}
