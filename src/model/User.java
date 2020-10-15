package model;

import java.sql.ResultSet;
import java.sql.SQLException;

public class User {

    private final int user_id;

    public User(ResultSet set) throws SQLException {
        this.user_id = set.getInt(1);
    }

    public int getUser_id() {
        return user_id;
    }

}
