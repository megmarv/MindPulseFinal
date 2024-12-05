package org.project.mindpulse.Controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.project.mindpulse.CoreModules.User;
import org.project.mindpulse.Database.ArticleHandler;
import org.project.mindpulse.Database.UserHandler;

import java.io.IOException;

public class UserController extends UserHandler implements GeneralFeatures{

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

    // Static field to hold the currently logged-in user
    private static User loggedInUser = null;

    // Getter for the logged-in user
    public static User getLoggedInUser() {
        return loggedInUser;
    }

    // Method to log out the user
    public static void logoutUser() {
        loggedInUser = null;
        System.out.println("User logged out.");
    }

    @FXML
    public void login(ActionEvent event) throws IOException {
        String username = this.usernameForLogin.getText();
        String password = this.passwordForLogin.getText();

        if(username.isEmpty() || password.isEmpty()) {
            displayError("username or password cannot be empty ! ");
        }

        if (userExists(username)) {  // Check if the user exists
            if (passwordCorrect(username, password)) {  // Check if the password is correct
                displayConfirmation("You have successfully logged in!");

                // Retrieve user details and create a User object
                User user = getUserDetails(username);  // Retrieve user details from the database or another source
                loggedInUser = user;
                System.out.println("User logged in: " + user.getUsername());

                // Redirect to the home page and pass the user details
                redirectToHomePage(event, user);
            } else {
                displayError("Password is incorrect, please try again.");
            }
        } else {
            displayError("User does not exist, please try again.");
        }
    }


    @FXML
    public void register(ActionEvent event) throws IOException {
        String name = this.nameForRegister.getText().trim();
        String email = this.emailForRegister.getText().trim();
        String username = this.usernameForRegister.getText().trim();
        String password = this.passwordForRegister.getText().trim();

        // Validations
        if (!name.matches("^[a-zA-Z ]{1,30}$")) {
            displayError("Name should only contain letters and be less than 30 characters");
            return;
        }

        if (!email.matches("^[a-zA-Z0-9._%+-]+@gmail\\.com$") || email.length() > 30) {
            displayError("Email should be a valid Gmail address and less than 30 characters");
            return;
        }

        if (username.length() > 30) {
            displayError("Username should be less than 30 characters");
            return;
        }

        if (!password.matches("^(?=.*[a-zA-Z])(?=.*\\d).{1,30}$")) {
            displayError("Password should be less than 30 characters and be a mix of letters and numbers");
            return;
        }

        // Check if user already exists
        if (userExists(username)) {
            displayError("This username is already taken, please try something else! ");
            return;
        }

        // Attempt to create the user
        if (createUser(name, email, username, password)) {
            displayConfirmation("You have successfully created an account! You can login now");
            redirectToLogInPage(event); // Redirect to the login page after successful registration
        } else {
            displayError("An error occurred. Please try again");
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

        ArticleHandler.populateUserHistory(loggedInUser);

    }

    @FXML
    private void redirectToRegistration(ActionEvent event) throws IOException {
        loadScene(event,"/org/project/mindpulse/NewUserAccount.fxml","Register",600,400);
    }

    @FXML
    private void redirectToLogInPage(ActionEvent event) throws IOException {
        loadScene(event,"/org/project/mindpulse/UserLogin.fxml","Login",600,400);
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
