package ui;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
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

    /**
     * This constructor establishes a connection with our primary database.
     */
    public Database() {
        try {
            Class.forName("org.postgresql.Driver");
            this.connection = DriverManager.getConnection(url, username, password);
            connection.setAutoCommit(false);
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
     * Returns a statement to be made to to the database.
     *
     * @return The SQL connection object.
     */
    public Statement getStatement() throws SQLException {
        return this.connection.createStatement();
    }
}
