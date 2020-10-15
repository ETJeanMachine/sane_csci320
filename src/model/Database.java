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
    // Constants
    //

    // These constants are used to parse a properties file that is securely stored on our computer, as to not disturb unwanted access to the database.
    private static final Properties prop = readPropertiesFile();
    private static final String username = prop.getProperty("username");
    private static final String password = prop.getProperty("password");
    private static final String url = "jdbc:postgresql://reddwarf.cs.rit.edu:5432/" + username;

    //
    // Attributes
    //

    private Connection connection = null;
    private HashMap<Integer, Album> databaseAlbums = new HashMap<>();
    private HashMap<Integer, Song> databaseSongs = new HashMap<>();
    private HashMap<Integer, Artist> databaseArtists = new HashMap<>();
    private HashMap<Integer, User> databaseUsers = new HashMap<>();

    /**
     * This constructor establishes a connection with our primary database.
     */
    public Database() {
        try {
            Class.forName("org.postgresql.Driver");
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
     * Parses the properties file for our values we don't want publicly seen (the username and password for our database).
     *
     * @return The parsed properties file.
     */
    private static Properties readPropertiesFile() {
        FileInputStream inputStream;
        Properties prop = null;
        try {
            inputStream = new FileInputStream("credentials.properties");
            prop = new Properties();
            prop.load(inputStream);
            inputStream.close();
        } catch (IOException fnfe) {
            fnfe.printStackTrace();
        }
        return prop;
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

    public Collection<Song> getDatabaseSongs() {
        return databaseSongs.values();
    }
}
