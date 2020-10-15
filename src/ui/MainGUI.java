package ui;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import model.Database;
import model.Song;

public class MainGUI extends Application {

    private Pane mainPane = new Pane();
    private FlowPane loginPane;
    private Database db;

    public void init() {
        // Sets up the login screen for the database.
        loginPane = new FlowPane();
        PasswordField passField = new PasswordField();
        passField.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    db = new Database(passField.getText());
                    mainPane.getChildren().remove(0);
                } catch (Exception e) {
                    passField.setText("");
                    loginPane.getChildren().add(new Label("Incorrect Login! Try again."));
                }
            }
        });
        loginPane.getChildren().add(passField);
        mainPane.getChildren().add(loginPane);
    }

    @Override
    public void start(Stage stage) {
        stage.setScene(new Scene(mainPane));
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
