import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

/**
 * The JavaFX GUI application that interacts with our database.
 */
public class Application {

    //
    // Constants
    //

    // These constants are used to parse a properties file that is securely stored on our computer, as to not disturb unwanted access to the database.
    private static final Properties prop = readPropertiesFile();
    private static final String username = prop.getProperty("username");
    private static final String password = prop.getProperty("password");
    private static final String url = "jdbc:postgresql://reddwarf.cs.rit.edu:5432/" + username;

    /**
     * Parses the properties file for our values we don't want publicly seen (the username and password for our database).
     *
     * @return The parsed properties file.
     */
    private static Properties readPropertiesFile() {
        FileInputStream inputStream = null;
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
     * @param args Any possible command line arguments.
     */
    public static void main(String[] args) {
        Connection c = null;
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("Success!");
    }
}
