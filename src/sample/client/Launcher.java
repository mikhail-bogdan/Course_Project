package sample.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Launcher extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        NetworkManager.Init();
        AccountManager.Init();
        MusicDatabase.Init();

        Parent root;
        if(AccountManager.IsUserLoggedIn())
            root = FXMLLoader.load(getClass().getResource("/resources/main.fxml"));
        else
            root = FXMLLoader.load(getClass().getResource("/resources/auth.fxml"));
        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("Music Player");
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
