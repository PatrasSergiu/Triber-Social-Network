package controller;


import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class newsController {

    @FXML
    private Label eventLabel;


    public void setLabel(String s) {
        this.eventLabel.setText(s);
    }

}
