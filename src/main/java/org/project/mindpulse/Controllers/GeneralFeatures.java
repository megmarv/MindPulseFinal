package org.project.mindpulse.Controllers;

import javafx.event.ActionEvent;

public interface GeneralFeatures {

    void displayConfirmation(String message);

    void displayError(String message);

    void Exit(ActionEvent event) throws Exception;

}
