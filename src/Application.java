import model.Song;
import ui.Database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Application {

    public static void main(String[] args) {
        Database db = new Database();
        try {
            Statement stmt = db.getStatement();
            Song song = new Song(0, stmt);
            System.out.println(song);
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
