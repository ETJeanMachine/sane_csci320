package ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.util.Callback;
import model.Database;
import model.Song;
import model.User;

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
        ArrayList<User> userList = db.getUserList();
        for(User u : userList) {
            MenuItem userItem = new MenuItem(u.getUser_id() + "");
            userItem.setOnAction(actionEvent -> {
                currentUser = u;
                renderLibrary();
            });
            userMenu.getItems().add(userItem);
        }
        Menu browseMenu = renderBrowse();
        menuBar.getMenus().addAll(userMenu, browseMenu);
        setTop(menuBar);
    }

    private Menu renderBrowse() {
        Menu browseMenu = new Menu("Browse");
        MenuItem browseSongs = new MenuItem("Songs");
        browseSongs.setOnAction(actionEvent -> {
            refreshScreen();
            try {
                TableView<Song> songs = buildSongTable(db.getAllSongs());
                setCenter(songs);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        MenuItem browseAlbums = new MenuItem("Albums");
        browseMenu.getItems().addAll(browseSongs, browseAlbums);
        return browseMenu;
    }

    private void renderLibrary() {
        refreshScreen();
        // Checking to see if we're swapping users.
        if(menuBar.getMenus().size() == 3) {
            menuBar.getMenus().remove(2);
        }
        Menu libraryMenu = new Menu("User #" + currentUser.getUser_id() + "'s Library");
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

    private TableView<Song> buildSongTable(ArrayList<Song> songs) {
        ObservableList<Song> songList = FXCollections.observableArrayList(songs);
        TableView<Song> songTable = new TableView<>(songList);

        // Creating our columns and setting their values.
        TableColumn<Song, Integer> songIdCol = new TableColumn<>("Song ID");
        TableColumn<Song, String> songTitleCol = new TableColumn<>("Title");
        TableColumn<Song, Integer> songLengthCol = new TableColumn<>("Length");
        TableColumn<Song, Integer> songPlayCountCol = new TableColumn<>("Play Count");
        songIdCol.setCellValueFactory(new PropertyValueFactory<>("song_id"));
        songTitleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        songLengthCol.setCellValueFactory(new PropertyValueFactory<>("length"));
        songPlayCountCol.setCellValueFactory(new PropertyValueFactory<>("play_count"));

        songTable.getColumns().setAll(songIdCol, songTitleCol, songLengthCol, songPlayCountCol);
        return songTable;
    }
}
