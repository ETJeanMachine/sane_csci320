package model;

import java.sql.ResultSet;
import java.sql.SQLException;

public class User {

    private int user_id;

    public User(ResultSet set) throws SQLException {
        this.user_id = set.getInt(0);
    }

}
