package ui;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.util.Callback;
import model.*;
import org.jetbrains.annotations.NotNull;

import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;

public class DatabaseGUI extends BorderPane {

    private final Database db;
    private final MenuBar menuBar = new MenuBar();
    private User currentUser;

    public DatabaseGUI(Database db) throws SQLException {
        this.db = db;
        renderGUI();
    }

    private void renderGUI() throws SQLException {
        Menu userMenu = new Menu("Users");
        ArrayList<User> userList = db.getAllItems(User.class);
        for(User u : userList) {
            MenuItem userItem = new MenuItem(u.getID() + "");
            userItem.setOnAction(actionEvent -> {
                currentUser = u;
                renderLibrary();
            });
            userMenu.getItems().add(userItem);
        }
        menuBar.getMenus().addAll(userMenu);
        renderBrowse();
        setTop(menuBar);
    }

    /**
     * This renders the browsing menu and query's the database whenever tabs are swapped between it.
     */
    private void renderBrowse() {
        Menu browseMenu = new Menu("Browse");
        MenuItem browseSongs = new MenuItem("Songs");
        MenuItem browseAlbums = new MenuItem("Albums");
        MenuItem browseArtists = new MenuItem("Artists");
        browseSongs.setOnAction(actionEvent -> {
            refreshScreen();
            TableBuilder<Song> builder = null;
            try {
                builder = new TableBuilder<>(db.getAllItems(Song.class));
                setCenter(builder.getTable());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        browseAlbums.setOnAction(actionEvent -> {
            refreshScreen();
            TableBuilder<Album> builder = null;
            try {
                builder = new TableBuilder<>(db.getAllItems(Album.class));
                setCenter(builder.getTable());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        browseArtists.setOnAction(actionEvent -> {
            refreshScreen();
            try {
                TableBuilder<Artist> builder = new TableBuilder<>(db.getAllItems(Artist.class));
                setCenter(builder.getTable());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        browseMenu.getItems().addAll(browseSongs, browseAlbums, browseArtists);
        menuBar.getMenus().add(browseMenu);
    }

    private void renderLibrary() {
        refreshScreen();
        // Checking to see if we're swapping users.
        if(menuBar.getMenus().size() == 3) {
            menuBar.getMenus().remove(2);
        }
        Menu libraryMenu = new Menu("User #" + currentUser.getID() + "'s Library");
        menuBar.getMenus().add(libraryMenu);
    }

    /**
     * Sets the center item to null.
     */
    private void refreshScreen() {
        if(getCenter() != null) {
            setCenter(null);
        }
    }
}
