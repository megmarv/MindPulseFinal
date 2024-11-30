package org.project.mindpulse.Controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.project.mindpulse.CoreModules.Article;

import java.io.IOException;

public class FirstPageController {

    @FXML private Button user;
    @FXML private Button admin;
    @FXML private Button exit;

    @FXML
    private void redirectToUserLogInPage(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/project/mindpulse/UserLogin.fxml"));
        Parent MainMenuWindow1 = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Sign in");
        Scene scene = new Scene(MainMenuWindow1, 600, 400);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void redirectToAdminLoginPage(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/project/mindpulse/AdminLogin.fxml"));
        Parent MainMenuWindow2 = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Sign in");
        Scene scene = new Scene(MainMenuWindow2, 600, 400);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void Exit(ActionEvent exit) throws IOException {

        Platform.exit();

    }

}