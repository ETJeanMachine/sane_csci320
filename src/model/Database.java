package model;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
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
     * This generates an arraylist of DataTypes that are sent to the UI.
     *
     * @param type The literal type for checking.
     * @param <T> The type parameter of our list.
     * @return An array list of DataType items.
     * @throws SQLException If there is an error parsing input.
     */
    public <T extends DataType> ArrayList<T> getAllItems(Class<T> type) throws SQLException {
        ArrayList<T> dataItems = new ArrayList<>();
        Statement stmt = connection.createStatement();
        String queryParam = "";
        if(type == User.class) {
            queryParam = "users";
        } else if(type == Song.class) {
            queryParam = "song";
        } else if(type == Artist.class) {
            queryParam = "artist";
        } else if(type == Album.class) {
            queryParam = "album";
        }
        ResultSet set = stmt.executeQuery("select * from " + queryParam);
        while(set.next()) {
            switch (queryParam) {
                case "users" -> dataItems.add((T) new User(set));
                case "song" -> dataItems.add((T) new Song(set));
                case "artist" -> dataItems.add((T) new Artist(set));
                case "album" -> dataItems.add((T) new Album(set));
            }
        }
        set.close();
        return dataItems;
    }

}
