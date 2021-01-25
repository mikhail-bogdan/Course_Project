package sample.client;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AuthController implements Initializable {
    @FXML
    TextField loginField;
    @FXML
    PasswordField passwordField;
    @FXML
    Button submitButton;
    @FXML
    Label errorLabel;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        submitButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                errorLabel.setText("");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final int error = AccountManager.LogIn(loginField.getText(), passwordField.getText());
                        if (error == 0) {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Parent root = FXMLLoader.load(getClass().getResource("/resources/main.fxml"));
                                        Stage primaryStage = ((Stage) loginField.getScene().getWindow());
                                        primaryStage.setScene(new Scene(root));
                                        primaryStage.setMinWidth(800);
                                        primaryStage.setWidth(800);
                                        primaryStage.setMinHeight(600);
                                        primaryStage.setHeight(600);
                                        primaryStage.show();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        } else {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    if(error == 1) {
                                        errorLabel.setText("Неправильный логин или пароль. Попробуйте еще раз.");
                                    } else if(error == 100) {
                                        errorLabel.setText("Нет доступа к сети. Попробуйте еще раз.");
                                    }
                                }
                            });
                        }
                    }
                }).start();
            }
        });
    }
}
