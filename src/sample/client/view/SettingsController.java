package sample.client.view;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import sample.client.model.AccountManager;

import java.net.URL;
import java.util.ResourceBundle;

public class SettingsController implements Initializable {
    @FXML
    public Label accountNameLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        accountNameLabel.setText(AccountManager.getLogin());
    }
}
