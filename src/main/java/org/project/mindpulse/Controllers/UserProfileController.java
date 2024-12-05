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
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import org.project.mindpulse.CoreModules.User;
import org.project.mindpulse.Database.UserHandler;

import java.io.IOException;

public class UserProfileController implements GeneralFeatures {

    @FXML private TextArea name;
    @FXML private TextArea email;
    @FXML private TextArea username;
    @FXML private TextArea password;

    @FXML private Button returnHome;

    @FXML
    private void initialize() {
        populateDetails(UserController.getLoggedInUser());
    }

    private void populateDetails(User user) {
        name.setText(user.getUsername());
        email.setText(user.getEmail());
        username.setText(user.getUsername());
        password.setText(user.getPassword());
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
    public void returnHome(ActionEvent exit) throws IOException {
        redirectToHomePage(exit,UserController.getLoggedInUser());
    }

    @FXML
    private void redirectToHomePage(ActionEvent event, User user) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/project/mindpulse/Home.fxml"));
        Parent mainMenuWindow = loader.load();

        // Get the HomeController instance and pass the user details
        HomeController homeController = loader.getController();
        homeController.updateUserLabel(user.getName()); // Update the home screen with user's name

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Home");
        Scene scene = new Scene(mainMenuWindow, 1100, 600);
        stage.setScene(scene);
        stage.show();

        UserController.populateUserHistory(UserController.getLoggedInUser());

    }

}
