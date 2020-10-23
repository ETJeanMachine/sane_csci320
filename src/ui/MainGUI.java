package ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Database;

import java.sql.SQLException;

public class MainGUI extends Application {

    public static final int GUI_WIDTH = 800;
    public static final int GUI_HEIGHT = 600;
    public static Stage stage;
    public static final Font mainFont = new Font("Arial", 14);

    private DatabaseGUI dbGUI;
    private final BorderPane mainPane = new BorderPane();
    private final Text title = new Text("S.A.N.E Database Manager");

    /**
     * Launches the application.
     * @param args null
     */
    public static void main(String[] args) {
        launch();
    }

    public static Text error(String errorText) {
        Text error = new Text(errorText);
        error.setFont(mainFont);
        error.setFill(Color.RED);
        return error;
    }

    /**
     * Renders the login, and then sets the database object to exist.
     */
    private void renderLogin() {
        VBox center = new VBox();
        HBox loginQuery = new HBox();
        PasswordField passField = new PasswordField();
        Text loginText = new Text("Please enter the database password for p320_14: ");
        loginText.setFont(mainFont);
        passField.setOnAction(actionEvent -> {
            // We attempt to login to the database when we enter the password,
            try {
                dbGUI = new DatabaseGUI(new Database(passField.getText()));
                mainPane.setTop(null);
                mainPane.setCenter(dbGUI);
            } catch (Exception e) {
                passField.setText("");
                String errorText;
                if(e.getMessage().contains("password")) {
                    errorText = "Incorrect login, try again.";
                } else {
                    errorText = "Internal database error, check stack trace.";
                    e.printStackTrace();
                }
                if(center.getChildren().size() == 2) {
                    center.getChildren().remove(1);
                }
                center.getChildren().add(1, MainGUI.error(errorText));
            }
        });
        loginQuery.getChildren().addAll(loginText, passField);
        loginQuery.setAlignment(Pos.TOP_CENTER);
        BorderPane.setAlignment(loginQuery, Pos.TOP_CENTER);
        center.setAlignment(Pos.TOP_CENTER);
        center.getChildren().add(0, loginQuery);
        mainPane.setCenter(center);
    }

    @Override
    public void init() {
        // Sets the header and the main pane for the application that will never change.
        Font titleFont = new Font("Arial Bold", 24);
        title.setFont(titleFont);
        title.setUnderline(true);
        BorderPane.setAlignment(title, Pos.TOP_CENTER);
        BorderPane.setMargin(title, new Insets(1, 0, 5, 0));
        mainPane.setTop(title);
        renderLogin();
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        // Setting up our stage components
        stage.setHeight(GUI_HEIGHT);
        stage.setWidth(GUI_WIDTH);
        stage.setTitle(title.getText());
        Scene scene = new Scene(mainPane);
        stage.setScene(scene);
        // Displaying the stage.
        stage.show();
    }

    @Override
    public void stop() {
        if(dbGUI != null) {
            try {
                dbGUI.closeDB();
            } catch (SQLException thrown) {
                thrown.printStackTrace();
            }
        }
    }
}