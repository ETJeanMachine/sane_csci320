import database.Database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Application {

    public static void main(String[] args) {
        Database db = new Database();
        try {
            Statement stmt = db.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery("select * from song;");
            while(rs.next()) {
                for(int i = 1; i < 5; i++) {
                    System.out.println(rs.getString(i));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
