package org;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MainController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onMainButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}
