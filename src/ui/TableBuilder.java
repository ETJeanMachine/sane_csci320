package ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Album;
import model.Artist;
import model.DataType;
import model.Song;

import java.sql.Date;
import java.util.ArrayList;

public class TableBuilder<S extends DataType> {

    // Our table that we are building.
    private final TableView<S> table;

    /**
     * A class that allows us to build tables from our data types.
     *
     * @param data The data we are constructed the data from.
     */
    public TableBuilder(ArrayList<S> data) {
        ObservableList<S> list = FXCollections.observableArrayList(data);
        table = new TableView<>(list);
        // Setting our ID column
        TableColumn<S, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("ID"));
        table.getColumns().add(idCol);

        // Building the tables based upon the data within them.
        S testData = data.get(0);
        if (testData instanceof Song) {
            buildSongTable();
        } else if (testData instanceof Album) {
            buildAlbumTable();
        } else if (testData instanceof Artist) {
            buildArtistTable();
        }
    }

    /**
     * Returns the table that has been built.
     *
     * @return a built table.
     */
    public TableView<S> getTable() {
        return this.table;
    }

    /**
     * This builds a table made out of songs.
     */
    private void buildSongTable() {
        TableColumn<S, String> titleCol = new TableColumn<>("Title");
        TableColumn<S, Integer> lengthCol = new TableColumn<>("Length");
        TableColumn<S, String> artistCol = new TableColumn<>("Artists");
        TableColumn<S, String> genreCol = new TableColumn<>("Genres");
        TableColumn<S, Integer> playCol = new TableColumn<>("Play Count");

        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        lengthCol.setCellValueFactory(new PropertyValueFactory<>("length"));
        artistCol.setCellValueFactory(new PropertyValueFactory<>("artists"));
        genreCol.setCellValueFactory(new PropertyValueFactory<>("genres"));
        playCol.setCellValueFactory(new PropertyValueFactory<>("play_count"));

        table.getColumns().addAll(titleCol, lengthCol, artistCol, genreCol, playCol);
    }

    /**
     * This builds a table made out of albums.
     */
    private void buildAlbumTable() {
        TableColumn<S, String> nameCol = new TableColumn<>("Album Name");
        TableColumn<S, String> artistCol = new TableColumn<>("Artists");
        TableColumn<S, Date> releaseDateCol = new TableColumn<>("Release Date");
        TableColumn<S, String> genreCol = new TableColumn<>("Genres");

        nameCol.setCellValueFactory(new PropertyValueFactory<>("album_name"));
        artistCol.setCellValueFactory(new PropertyValueFactory<>("artists"));
        releaseDateCol.setCellValueFactory(new PropertyValueFactory<>("release_date"));
        genreCol.setCellValueFactory(new PropertyValueFactory<>("genres"));

        table.getColumns().addAll(nameCol, artistCol, releaseDateCol, genreCol);
    }

    /**
     * This builds a table made out of artists.
     */
    private void buildArtistTable() {
        TableColumn<S, String> nameCol = new TableColumn<>("Artist Name");
        TableColumn<S, Date> birthCol = new TableColumn<>("Date of Birth");

        nameCol.setCellValueFactory(new PropertyValueFactory<>("artist_name"));
        birthCol.setCellValueFactory(new PropertyValueFactory<>("dob"));

        table.getColumns().addAll(nameCol, birthCol);
    }
}
