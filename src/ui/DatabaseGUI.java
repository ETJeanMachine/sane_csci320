package ui;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import model.*;

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

    public void closeDB() throws SQLException {
        db.closeConnection();
    }

    private void renderGUI() throws SQLException {
        Menu userMenu = new Menu("Users");
        MenuItem loginUser = new MenuItem("Login");
        loginUser.setOnAction(actionEvent -> {
            Popup popup = loginQuery();
            popup.show(MainGUI.stage);
        });
        userMenu.getItems().add(loginUser);
        menuBar.getMenus().addAll(userMenu);
        renderBrowse();
        setTop(menuBar);
    }

    /**
     * This renders the browsing menu and query's the database whenever tabs are swapped between it.
     */
    private void renderBrowse() {
        Menu browseMenu = new Menu("Browse");
        // You can only browse songs and albums in our GUI.
        MenuItem browseSongs = new MenuItem("Songs");
        MenuItem browseAlbums = new MenuItem("Albums");
        // Our internal components that are universal.
        VBox center = new VBox();
        HBox searchQuery = new HBox();
        Text entry = new Text("Enter a name: ");
        TextField search = new TextField();
        entry.setFont(MainGUI.mainFont);
        searchQuery.getChildren().add(0, entry);
        searchQuery.getChildren().add(1, search);
        center.getChildren().add(0, searchQuery);
        // Sets up the screen that allows us to browse songs within the database.
        browseSongs.setOnAction(actionEvent -> {
            refreshScreen();
            // Removing any search queries.
            refreshCenter(center);
            search.setOnAction(searchAction -> {
                refreshCenter(center);
                try {
                    ArrayList<Song> songs = db.searchForSongs(search.getText());
                    if(songs.size() == 0) {
                        center.getChildren().add(1, MainGUI.error("No songs found from search!"));
                    } else {
                        TableBuilder<Song> builder = new TableBuilder<>(songs);
                        builder.getTable().setRowFactory(tableView -> {
                            TableRow<Song> row = new TableRow<>();
                            ContextMenu menu = new ContextMenu();
                            MenuItem add = new MenuItem("Add song to library");
                            row.setOnMouseClicked(mouseEvent -> {
                                Song s = row.getItem();
                                if(s != null) {
                                    try {
                                        db.addSongToUserLibrary(currentUser, s);
                                    } catch (SQLException e) {
                                        setBottom(MainGUI.error(e.getMessage()));
                                    }
                                    menu.hide();
                                    menu.show(row, mouseEvent.getScreenX(), mouseEvent.getScreenY());
                                }
                            });
                            if(currentUser != null) {
                                menu.getItems().add(add);
                            }
                            return row;
                        });
                        center.getChildren().add(1, builder.getTable());
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
            setCenter(center);
        });
        browseAlbums.setOnAction(actionEvent -> {
            refreshScreen();
            refreshCenter(center);
            search.setOnAction(searchAction -> {
                refreshCenter(center);
                try {
                    ArrayList<Album> albums = db.searchForAlbums(search.getText());
                    if(albums.size() == 0) {
                        center.getChildren().add(1, MainGUI.error("No albums found from search!"));
                    } else {
                        TableBuilder<Album> builder = new TableBuilder<>(albums);
                        // Setting our context menus for our table.
                        builder.getTable().setRowFactory(tableView -> {
                            TableRow<Album> row = new TableRow<>();
                            ContextMenu menu = new ContextMenu();
                            MenuItem songs = new MenuItem("See songs in album");
                            MenuItem add = new MenuItem("Add album to library");
                            row.setOnMouseClicked(mouseEvent -> {
                                Album a = row.getItem();
                                if(a != null) {
                                    songs.setOnAction(event -> {
                                        ArrayList<Song> s = a.getSongs();
                                        if(s.size() != 0) {
                                            TableBuilder<Song> songTableBuilder = new TableBuilder<>(s);
                                            // Adding our track number
                                            TableColumn<Song, Integer> trackNumCol = new TableColumn<>("Track Number");
                                            trackNumCol.setCellValueFactory(new PropertyValueFactory<>("track_number"));
                                            TableView<Song> table = songTableBuilder.getTable();
                                            table.getColumns().add(trackNumCol);
                                            setBottom(table);
                                        } else {
                                            setBottom(MainGUI.error("No songs in album!"));
                                        }
                                    });
                                    add.setOnAction(event -> {

                                    });
                                    menu.hide();
                                    menu.show(row, mouseEvent.getScreenX(), mouseEvent.getScreenY());
                                }
                            });
                            if(currentUser != null) {
                                menu.getItems().add(add);
                            }
                            menu.getItems().add(songs);
                            return row;
                        });
                        center.getChildren().add(1, builder.getTable());
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
            setCenter(center);
        });
        browseMenu.getItems().addAll(browseSongs, browseAlbums);
        menuBar.getMenus().add(browseMenu);
    }

    private void renderLibrary() {
        refreshScreen();
        // Checking to see if we're swapping users.
        if (menuBar.getMenus().size() == 3) {
            menuBar.getMenus().remove(2);
        }
        Menu libraryMenu = new Menu("User #" + currentUser.getID() + "'s Library");
        MenuItem librarySongs = new MenuItem("Songs");
        MenuItem libraryAlbums = new MenuItem("Albums");
        librarySongs.setOnAction(actionEvent -> {
            refreshScreen();
            ArrayList<Song> songLibrary = currentUser.getSongLibrary();
            if(songLibrary.size() == 0) {
                setCenter(MainGUI.error("User #" + currentUser.getID() + "'s song library is empty!"));
            } else {
                TableBuilder<Song> songs = new TableBuilder<>(currentUser.getSongLibrary());
                setCenter(songs.getTable());
            }
        });
        libraryAlbums.setOnAction(actionEvent -> {
            refreshScreen();
            ArrayList<Album> albumLibrary = currentUser.getAlbumLibrary();
            if(albumLibrary.size() == 0) {
                setCenter(MainGUI.error("User #" + currentUser.getID() + "'s album library is empty!"));
            } else {
                TableBuilder<Album> albums = new TableBuilder<>(currentUser.getAlbumLibrary());
                setCenter(albums.getTable());
            }
        });
        libraryMenu.getItems().addAll(librarySongs, libraryAlbums);
        menuBar.getMenus().add(libraryMenu);
    }

    /**
     * Sets the center item to null so we can refresh the screen.
     */
    private void refreshScreen() {
        if (getCenter() != null) {
            setCenter(null);
        }
        if(getBottom() != null) {
            setBottom(null);
        }
    }

    private void refreshCenter(VBox center) {
        while(center.getChildren().size() > 1) {
            center.getChildren().remove(1);
        }
    }

    private Popup loginQuery() {
        Popup popup = new Popup();
        VBox content = new VBox();
        HBox query = new HBox();
        CornerRadii radii = new CornerRadii(5);
        content.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, radii, BorderStroke.THIN)));
        content.setBackground(new Background(new BackgroundFill(Color.WHITESMOKE, radii, Insets.EMPTY)));
        Text t = new Text("Login with an existing ID or enter a new one:");
        t.setFont(MainGUI.mainFont);
        TextField field = new TextField();
        HBox.setMargin(field, new Insets(5));
        HBox.setMargin(t, new Insets(5));

        field.setOnAction(actionEvent -> {
            try {
                int id = Integer.parseInt(field.getText());
                currentUser = db.fetchUser(id);
                popup.hide();
                renderLibrary();
            } catch (SQLException e) {
                content.getChildren().add(1, MainGUI.error("Internal database error!"));
                e.printStackTrace();
                if(content.getChildren().size() == 3) {
                    content.getChildren().remove(2);
                }
            } catch (NumberFormatException e) {
                content.getChildren().add(1, MainGUI.error("Please enter an integer."));
                if(content.getChildren().size() == 3) {
                    content.getChildren().remove(2);
                }
            }
        });

        query.getChildren().addAll(t, field);
        content.getChildren().add(query);
        popup.getContent().add(content);
        popup.centerOnScreen();
        return popup;
    }
}
