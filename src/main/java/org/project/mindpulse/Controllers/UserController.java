package org.project.mindpulse.Controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class UserController implements GeneralFeatures {

    @FXML private Button login;
    @FXML private Button register;
    @FXML private Button exit;
    @FXML private Button goToRegistration;
    @FXML private Button goToLogin;

    @FXML private TextField usernameForLogin;
    @FXML private PasswordField passwordForLogin;

    @FXML private TextField nameForRegister;
    @FXML private TextField emailForRegister;
    @FXML private TextField usernameForRegister;
    @FXML private PasswordField passwordForRegister;


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

}
