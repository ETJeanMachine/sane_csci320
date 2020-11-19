package ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.util.Pair;
import model.*;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

public class DatabaseGUI extends BorderPane {

    //
    // Attributes
    //

    private final Database db;
    private final MenuBar menuBar = new MenuBar();
    private User currentUser;

    /**
     * This is the constructor for our Database GUI.
     *
     * @param db the database of the GUI.
     */
    public DatabaseGUI(Database db) throws SQLException {
        this.db = db;
        renderGUI();
    }

    /**
     * This closes the database.
     *
     * @throws SQLException if there is an error on closure.
     */
    public void closeDB() throws SQLException {
        db.closeConnection();
    }

    /**
     * This renders the GUI.
     */
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
        renderAnalytics();
        setTop(menuBar);
    }

    private void renderAnalytics() throws SQLException {
        VBox analyticBox = new VBox();
        if(currentUser != null) {
            Text lengths = new Text("Mean Lengths: ");
            lengths.setFont(MainGUI.boldFont);
            Text albumLen = new Text("Mean User Album Length: " + db.meanAlbumLength(currentUser));
            albumLen.setFont(MainGUI.mainFont);
            Text songLen = new Text("Mean User Song Length: " + db.meanSongLength(currentUser));
            songLen.setFont(MainGUI.mainFont);

            GridPane dataAnalytics = new GridPane();
            dataAnalytics.setAlignment(Pos.CENTER);
            dataAnalytics.setPrefSize(MainGUI.GUI_WIDTH, 400);

            VBox ownedGenres = new VBox();
            Text ownedGenresText = new Text("Most Owned Genres: ");
            ownedGenresText.setFont(MainGUI.boldFont);
            ListView<?> ownedGenresList = listContent(db.mostOwnedGenres(currentUser));
            ownedGenres.getChildren().addAll(ownedGenresText, ownedGenresList);

            VBox ownedArtists = new VBox();
            Text ownedArtistsText = new Text("Most Owned Artists: ");
            ownedArtistsText.setFont(MainGUI.boldFont);
            ListView<?> ownedArtistsList = listContent(db.mostOwnedArtists(currentUser));
            ownedArtists.getChildren().addAll(ownedArtistsText, ownedArtistsList);

            dataAnalytics.addRow(0, ownedGenres, ownedArtists);

            VBox playedSongs = new VBox();
            Text playedSongsText = new Text("Most Played Songs: ");
            playedSongsText.setFont(MainGUI.boldFont);
            ListView<?> playedSongsList = listContent(db.mostPlayedSongs(currentUser));
            playedSongs.getChildren().addAll(playedSongsText, playedSongsList);

            VBox playedGenres = new VBox();
            Text playedGenresText = new Text("Most Played Genres: ");
            playedGenresText.setFont(MainGUI.boldFont);
            ListView<?> playedGenresList = listContent(db.mostPlayedGenres(currentUser));
            playedGenres.getChildren().addAll(playedGenresText, playedGenresList);

            dataAnalytics.addRow(1, playedSongs, playedGenres);

            Text recommendText = new Text("Recommended Songs: ");
            recommendText.setFont(MainGUI.boldFont);
            ObservableList<String> items = FXCollections.observableArrayList(db.recommendedSongs(currentUser));
            ListView<String> recommendedSongs = new ListView<>(items);

            analyticBox.getChildren().addAll(lengths, albumLen, songLen, dataAnalytics, recommendText, recommendedSongs);
        } else {
            Text totalGenres = new Text("Most Played Genres Globally: ");
            totalGenres.setFont(MainGUI.boldFont);
            analyticBox.getChildren().add(totalGenres);
            analyticBox.getChildren().add(listContent(db.totalGenreOwnership()));
        }
        setCenter(analyticBox);
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
                    if (songs.size() == 0) {
                        center.getChildren().add(1, MainGUI.error("No songs found from search!"));
                    } else {
                        TableBuilder<Song> builder = new TableBuilder<>(songs);
                        builder.getTable().setRowFactory(tableView -> {
                            TableRow<Song> row = new TableRow<>();
                            ContextMenu menu = new ContextMenu();
                            MenuItem add = new MenuItem("Add song to library");
                            row.setOnMouseClicked(mouseEvent -> {
                                Song s = row.getItem();
                                if (s != null && currentUser != null) {
                                    add.setOnAction(event -> {
                                        try {
                                            db.addSongToUserLibrary(currentUser, s);
                                        } catch (Exception ignored) {
                                        }
                                    });
                                    menu.hide();
                                    if (!currentUser.getSongLibrary().contains(s)) {
                                        menu.show(row, mouseEvent.getScreenX(), mouseEvent.getScreenY());
                                    }
                                }
                            });
                            menu.getItems().add(add);
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
                    if (albums.size() == 0) {
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
                                if (a != null) {
                                    songs.setOnAction(event -> {
                                        ArrayList<Song> s = a.getSongs();
                                        if (s.size() != 0) {
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
                                        try {
                                            db.addAlbumToUserLibrary(currentUser, a);
                                        } catch (Exception ignored) {
                                        }
                                    });
                                    if (currentUser == null || currentUser.getAlbumLibrary().contains(a)) {
                                        menu.getItems().remove(add);
                                    }
                                    menu.hide();
                                    menu.show(row, mouseEvent.getScreenX(), mouseEvent.getScreenY());
                                }
                            });
                            menu.getItems().add(add);
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

    /**
     * This method renders the user library component of our GUI.
     */
    private void renderLibrary() {
        refreshScreen();
        // Checking to see if we're swapping users.
        if (menuBar.getMenus().size() == 3) {
            menuBar.getMenus().remove(2);
        }
        Menu libraryMenu = new Menu("User #" + currentUser.getID() + "'s Library");
        MenuItem librarySongs = new MenuItem("Songs");
        MenuItem libraryAlbums = new MenuItem("Albums");
        MenuItem libraryArtists = new MenuItem("Artists");
        // Setting up our different menu items.
        librarySongs.setOnAction(actionEvent -> {
            refreshScreen();
            ArrayList<Song> songLibrary = currentUser.getSongLibrary();
            if (songLibrary.size() == 0) {
                setCenter(MainGUI.error("User #" + currentUser.getID() + "'s song library is empty!"));
            } else {
                TableBuilder<Song> songs = new TableBuilder<>(songLibrary);
                TableView<Song> songTable = songs.getTable();
                songTable.setRowFactory(tableView -> {
                    TableRow<Song> row = new TableRow<>();
                    ContextMenu menu = new ContextMenu();
                    MenuItem play = new MenuItem("Play song");
                    MenuItem times = new MenuItem("View times played");
                    row.setOnMouseClicked(mouseEvent -> {
                        Song s = row.getItem();
                        play.setOnAction(event -> {
                            try {
                                db.playSong(currentUser, s);
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        });
                        times.setOnAction(event -> {
                            try {
                                ArrayList<Timestamp> timestamps = db.getTimeStamps(currentUser, s);
                                ListView<Timestamp> timeTable = new ListView<>();
                                timeTable.setItems(FXCollections.observableArrayList(timestamps));
                                setBottom(timeTable);
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        });
                        if (s.getPlay_count() == 0) {
                            menu.getItems().remove(times);
                        }
                        menu.hide();
                        menu.show(row, mouseEvent.getScreenX(), mouseEvent.getScreenY());
                    });
                    menu.getItems().add(times);
                    menu.getItems().add(play);
                    return row;
                });
                setCenter(songs.getTable());
            }
        });
        libraryAlbums.setOnAction(actionEvent -> {
            refreshScreen();
            ArrayList<Album> albumLibrary = currentUser.getAlbumLibrary();
            if (albumLibrary.size() == 0) {
                setCenter(MainGUI.error("User #" + currentUser.getID() + "'s album library is empty!"));
            } else {
                TableBuilder<Album> albums = new TableBuilder<>(albumLibrary);
                setCenter(albums.getTable());
            }
        });
        libraryArtists.setOnAction(actionEvent -> {
            refreshScreen();
            ArrayList<Artist> artistLibrary = currentUser.getArtistLibrary();
            if (artistLibrary.size() == 0) {
                setCenter(MainGUI.error("User #" + currentUser.getID() + "'s artist library is empty!"));
            } else {
                TableBuilder<Artist> artists = new TableBuilder<>(artistLibrary);
                setCenter(artists.getTable());
            }
        });
        libraryMenu.getItems().addAll(librarySongs, libraryAlbums, libraryArtists);
        menuBar.getMenus().add(libraryMenu);
    }

    /**
     * Sets the center item to null so we can refresh the screen.
     */
    private void refreshScreen() {
        if (getCenter() != null) {
            setCenter(null);
        }
        if (getBottom() != null) {
            setBottom(null);
        }
    }

    private void refreshCenter(VBox center) {
        while (center.getChildren().size() > 1) {
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
                renderAnalytics();
            } catch (SQLException e) {
                content.getChildren().add(1, MainGUI.error("Internal database error!"));
                e.printStackTrace();
                if (content.getChildren().size() == 3) {
                    content.getChildren().remove(2);
                }
            } catch (NumberFormatException e) {
                content.getChildren().add(1, MainGUI.error("Please enter an integer."));
                if (content.getChildren().size() == 3) {
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

    private ListView<Pair<String, Integer>> listContent(ArrayList<Pair<String, Integer>> list) {
        ObservableList<Pair<String, Integer>> items = FXCollections.observableArrayList(list);
        return new ListView<>(items);
    }
}
