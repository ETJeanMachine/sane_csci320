package ui;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import model.Database;

public class MainGUI extends Application {

    public static final int GUI_WIDTH = 800;
    public static final int GUI_HEIGHT = 600;
    public final Font mainFont = new Font("Arial", 14);

    private final GridPane mainPane = new GridPane();
    private final GridPane loginPane = new GridPane();
    private final String title = "S.A.N.E Database Manager";
    private DatabaseGUI dbGUI;

    /**
     * Launches the application.
     * @param args null
     */
    public static void main(String[] args) {
        launch();
    }

    /**
     * Renders the login, and then sets the database object to exist.
     */
    private void renderLogin() {
        PasswordField passField = new PasswordField();
        Text loginText = new Text("Please enter the database password for p320_14: ");
        loginText.setFont(mainFont);
        passField.setOnAction(actionEvent -> {
            // We attempt to login to the database when we enter the password,
            try {
                dbGUI = new DatabaseGUI(new Database(passField.getText()));
                mainPane.getChildren().remove(loginPane);
                mainPane.addRow(1, dbGUI);
            } catch (Exception e) {
                passField.setText("");
                // Making sure we don't set it multiple times.
                if (loginPane.getRowCount() != 2) {
                    Text error = new Text("Login failed, try again.");
                    error.setFont(mainFont);
                    error.setFill(Color.RED);
                    loginPane.addRow(1, error);
                }
            }
        });
        loginPane.addRow(0, loginText, passField);
        loginPane.setAlignment(Pos.CENTER);
        mainPane.addRow(1, loginPane);
    }

    public void init() {
        // Sets the header and the main pane for the application that will never change.
        Text titleHeader = new Text(title);
        Font titleFont = new Font("Arial Bold", 24);
        titleHeader.setFont(titleFont);
        titleHeader.setUnderline(true);
        GridPane.setHalignment(titleHeader, HPos.CENTER);
        mainPane.addRow(0, titleHeader);
        mainPane.setAlignment(Pos.TOP_CENTER);
        mainPane.setVgap(5);
        renderLogin();
    }

    @Override
    public void start(Stage stage) {
        // Setting up our stage components
        stage.setResizable(false);
        stage.setHeight(GUI_HEIGHT);
        stage.setWidth(GUI_WIDTH);
        stage.setTitle(title);
        Scene scene = new Scene(mainPane);
        stage.setScene(scene);
        // Displaying the stage.
        stage.show();
    }
}
