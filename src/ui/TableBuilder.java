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

    private final TableView<S> table;

    /**
     * A class that allows us to build tables from our data types.
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
        if(testData instanceof Song) {
            buildSongTable();
        } else if(testData instanceof Album) {
            buildAlbumTable();
        } else if(testData instanceof Artist) {
            buildArtistTable();
        }
    }

    public TableView<?> getTable() {
        return this.table;
    }

    /**
     * This builds a table made out of songs.
     */
    private void buildSongTable() {
        TableColumn<S, String> titleCol = new TableColumn<>("Title");
        TableColumn<S, Integer> lengthCol = new TableColumn<>("Length");
        TableColumn<S, Integer> playCountCol = new TableColumn<>("Play Count");

        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        lengthCol.setCellValueFactory(new PropertyValueFactory<>("length"));
        playCountCol.setCellValueFactory(new PropertyValueFactory<>("play_count"));

        table.getColumns().addAll(titleCol, lengthCol, playCountCol);
    }

    private void buildAlbumTable() {
        TableColumn<S, String> nameCol = new TableColumn<>("Album Name");
        TableColumn<S, Date> releaseDateCol = new TableColumn<>("Release Date");

        nameCol.setCellValueFactory(new PropertyValueFactory<>("album_name"));
        releaseDateCol.setCellValueFactory(new PropertyValueFactory<>("release_date"));

        table.getColumns().addAll(nameCol, releaseDateCol);
    }

    private void buildArtistTable() {
        TableColumn<S, String> nameCol = new TableColumn<>("Artist Name");
        TableColumn<S, Date> birthCol = new TableColumn<>("Date of Birth");

        nameCol.setCellValueFactory(new PropertyValueFactory<>("artist_name"));
        birthCol.setCellValueFactory(new PropertyValueFactory<>("dob"));

        table.getColumns().addAll(nameCol, birthCol);
    }
}