package org.project.mindpulse.Controllers;

import javafx.event.ActionEvent;

import java.io.IOException;

public interface GeneralFeatures {

    void displayConfirmation(String message);

    void displayError(String message);

    void Exit(ActionEvent event) throws Exception;

    void loadScene(ActionEvent event, String fxmlPath, String title, int width, int height) throws IOException;

}
