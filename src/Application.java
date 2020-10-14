import java.sql.*;

/**
 * The JavaFX GUI application that interacts with our database.
 */
public class Application {

    // TODO
    public static void main(String[] args) {
        Connection c = null;
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager.getConnection("jdbc:postgresql://reddwarf.cs.rit.edu:5432/p320_14", "p320_14", "");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": " + e.getMessage());
        }
    }
}
