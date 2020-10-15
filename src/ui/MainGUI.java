package ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import model.Database;
import model.Song;

import java.awt.event.ActionEvent;

public class MainGUI extends Application {

    private FlowPane pane;

    public void init() {
        pane = new FlowPane();
        PasswordField passField = new PasswordField();

        pane.getChildren().add(passField);
    }

    @Override
    public void start(Stage stage) {
        //Database db = new Database(PASSWORD);
        /*
        for(Song song : db.getDatabaseSongs()) {
            Label l = new Label(song.toString());
            pane.getChildren().add(l);
        }
        */
        stage.setScene(new Scene(pane));
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
