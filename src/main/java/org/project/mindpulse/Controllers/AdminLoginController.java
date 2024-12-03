package org.project.mindpulse.Controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.project.mindpulse.CoreModules.User;
import org.project.mindpulse.Database.AdminHandler;
import org.project.mindpulse.Database.UserHandler;

import java.io.IOException;

public class AdminLoginController extends AdminHandler implements GeneralFeatures {

    @FXML private TextField username;
    @FXML private PasswordField password;

    @FXML private Button loginButton;
    @FXML private Button backToMain;
    @FXML private Button exitButton;

    @FXML
    public void login(ActionEvent event) throws IOException {
        String username = this.username.getText();
        String password = this.password.getText();

        if (adminExists(username)) {  // Check if the user exists
            if (passwordCorrect(username, password)) {  // Check if the password is correct
                displayConfirmation("Welcome back, Admin!");
                redirectToAdminPortal(event);
            } else {
                displayError("Password is incorrect, please try again.");
            }
        } else {
            displayError("Admin does not exist ! please try again !");
        }
    }

    @FXML
    public void displayConfirmation(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void displayError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void Exit(ActionEvent exit) throws IOException {

        Platform.exit();

    }

    @FXML
    private void redirectToAdminPortal(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/project/mindpulse/AdminPage.fxml"));
        Parent mainMenuWindow = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Admin Portal");
        Scene scene = new Scene(mainMenuWindow, 1100, 600);
        stage.setScene(scene);
        stage.show();

    }

    @FXML
    private void backToMain(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/project/mindpulse/FirstPage.fxml"));
        Parent mainMenuWindow = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("MindPulse");
        Scene scene = new Scene(mainMenuWindow, 600, 400);
        stage.setScene(scene);
        stage.show();

    }

}
