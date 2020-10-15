package ui;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import model.Database;
import model.Song;

public class MainGUI extends Application {

    private FlowPane pane;
    private Database db;

    public void init() {
        pane = new FlowPane();
        PasswordField passField = new PasswordField();
        passField.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    db = new Database(passField.getText());
                    pane.getChildren().remove(0);
                    pane.getChildren().remove(1);
                } catch (Exception e) {
                    pane.getChildren().add(new Label("Incorrect Login! Try again."));
                }
            }
        });
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
