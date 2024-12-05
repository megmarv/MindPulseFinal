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

    @FXML private PasswordField password;

    @FXML private Button loginButton;
    @FXML private Button backToMain;
    @FXML private Button exitButton;

    @FXML
    public void login(ActionEvent event) throws IOException {
        String password = this.password.getText();

        if (isAdminPasswordCorrect(password)) { // Directly check if the password is correct
            displayConfirmation("Welcome back, Admin!");
            redirectToAdminPortal(event);
        } else {
            displayError("Password is incorrect, please try again.");
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
    public void Exit(ActionEvent exit) throws IOException { Platform.exit(); }

    @FXML
    private void redirectToAdminPortal(ActionEvent event) throws IOException {
        loadScene(event,"/org/project/mindpulse/AdminPage.fxml","Admin Portal",1100,600);
    }

    @FXML
    private void backToMain(ActionEvent event) throws IOException {
        loadScene(event,"/org/project/mindpulse/AdminPage.fxml","MindPulse",600,400);
    }

    public void loadScene(ActionEvent event, String fxmlPath, String title, int width, int height) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent window = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle(title);
        stage.setScene(new Scene(window, width, height));
        stage.show();
    }

}
