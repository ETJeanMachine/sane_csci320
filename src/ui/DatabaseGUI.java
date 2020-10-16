package ui;

import javafx.geometry.Insets;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import model.*;

import java.sql.SQLException;

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
        if (menuBar.getMenus().size() == 3) {
            menuBar.getMenus().remove(2);
        }
        Menu libraryMenu = new Menu("User #" + currentUser.getID() + "'s Library");
        menuBar.getMenus().add(libraryMenu);
    }

    /**
     * Sets the center item to null so we can refresh the screen.
     */
    private void refreshScreen() {
        if (getCenter() != null) {
            setCenter(null);
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
            Text error = new Text();
            try {
                int id = Integer.parseInt(field.getText());
                currentUser = db.fetchUser(id);
                renderLibrary();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

            popup.hide();
        });

        query.getChildren().addAll(t, field);
        content.getChildren().add(query);
        popup.getContent().add(content);
        popup.centerOnScreen();
        return popup;
    }
}
