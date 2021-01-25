package sample.client.view;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import sample.client.model.AccountManager;
import sample.client.model.NetworkManager;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;

public class PlaylistsController implements Initializable {

    String[] playlistsNames;
    LinkedList<LinkedList<Integer>> playlists;

    @FXML
    FlowPane container;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(NetworkManager.IsOffline()) return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                Pair<String[], LinkedList<LinkedList<Integer>>> data = AccountManager.GetUserPlaylists();
                if (data == null) return;
                playlistsNames = data.getKey();
                playlists = data.getValue();
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < playlistsNames.length; i++) {
                            Parent root = null;
                            try {
                                root = FXMLLoader.load(getClass().getResource("/resources/playlistCell.fxml"));
                            } catch (IOException ignored) {}
                            ((Label)(((VBox)root).getChildren().get(1))).setText(playlistsNames[i]);
                            container.getChildren().add(root);
                        }
                    }
                });
            }
        }).start();
    }
}
