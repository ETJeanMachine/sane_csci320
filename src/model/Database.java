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
    private Connection connection = null;
    // These hashmaps store the database items.
    private HashMap<Integer, Album> databaseAlbums = new HashMap<>();
    private HashMap<Integer, Song> databaseSongs = new HashMap<>();
    private HashMap<Integer, Artist> databaseArtists = new HashMap<>();
    private HashMap<Integer, User> databaseUsers = new HashMap<>();

    /**
     * This constructor establishes a connection with our primary database.
     */
    public Database(String password) {
        try {
            Class.forName("org.postgresql.Driver");
            // These constants are used to parse a properties file that is securely stored on our computer, as to not disturb unwanted access to the database.
            String username = "p320_14";
            String url = "jdbc:postgresql://reddwarf.cs.rit.edu:5432/" + username;
            this.connection = DriverManager.getConnection(url, username, password);
            connection.setAutoCommit(false);
            buildDatabase();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * This interprets and builds our Database into a database Java model.
     * @throws SQLException If there is an error while parsing the database.
     */
    private void buildDatabase() throws SQLException {
        Statement stmt = connection.createStatement();
        ResultSet set = stmt.executeQuery("select * from song");
        while(set.next()) {
            Song thisSong = new Song(set);
            databaseSongs.put(thisSong.getSong_id(), thisSong);
        }
    }

    /**
     * Returns all the songs within the database.
     * @return A collection holding every single song.
     */
    public Collection<Song> getDatabaseSongs() {
        return databaseSongs.values();
    }
}
