package model;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Properties;

/**
 * The class that connects us to the primary database of the system.
 *
 * @author Eric Jean
 */
public class Database {


    //
    // Attributes
    //

    // This is the object that connects us to our database.
    private final Connection connection;

    /**
     * This constructor establishes a connection with our primary database.
     */
    public Database(String password) throws Exception {
            Class.forName("org.postgresql.Driver");
            // These constants are used to parse a properties file that is securely stored on our computer, as to not disturb unwanted access to the database.
            String username = "p320_14";
            String url = "jdbc:postgresql://reddwarf.cs.rit.edu:5432/" + username;
            this.connection = DriverManager.getConnection(url, username, password);
            connection.setAutoCommit(false);
    }

    /**
     * Returns all the songs within the database.
     *
     * @return An arraylist holding all of the users in the database.
     */
    public ArrayList<User> getUserList() throws SQLException {
        Statement stmt = connection.createStatement();
        ResultSet currentUser = stmt.executeQuery("select * from users");
        ArrayList<User> userList = new ArrayList<>();
        while (currentUser.next()) {
            userList.add(new User(currentUser));
        }
        return userList;
    }
}
