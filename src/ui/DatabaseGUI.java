package ui;

import javafx.geometry.Pos;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.GridPane;
import model.Database;
import model.User;

import java.sql.SQLException;
import java.util.ArrayList;

public class DatabaseGUI extends GridPane {

    private Database db;
    private MenuBar menuBar = new MenuBar();
    private User currentUser;

    public DatabaseGUI(Database db) throws SQLException {
        this.db = db;
        renderGUI();
    }

    private void renderGUI() throws SQLException {
        Menu userList = new Menu("Users");
        ArrayList<User> users = null;
        users = db.getUserList();
        for(User u : users) {
            userList.getItems().add(new MenuItem(u.getUser_id() + ""));
        }
        menuBar.getMenus().add(userList);
        addRow(0, menuBar);
    }

}
