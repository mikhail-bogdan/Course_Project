package sample.client.view;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.WindowEvent;
import sample.client.model.*;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class UIController implements Initializable, OnStateChangedEvent, OnNextPrevChangedEvent {

    public static MusicPlayer player;

    @FXML
    BorderPane root;
    @FXML
    MusicHorizontalBarController controller;
    @FXML
    Button playPauseButton;
    @FXML
    Button prevButton;
    @FXML
    Button nextButton;

    @FXML
    ImageView playPauseImageView;
    @FXML
    ImageView prevImageView;
    @FXML
    ImageView nextImageView;

    Image imagePlayButton;
    Image imagePauseButton;
    Image imagePrevButton;
    Image imageNextButton;


    @FXML
    ImageView audioImageView;
    @FXML
    Label audioNameLabel;
    @FXML
    Label authorLabel;

    @FXML
    MyButton allAudioButton;
    @FXML
    MyButton playlistsButton;
    @FXML
    MyButton settingsButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        final UIController control = this;

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                allAudioButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        allAudioButton.set();
                        playlistsButton.unSet();
                        settingsButton.unSet();
                        try {
                            Parent node = FXMLLoader.load(getClass().getResource("/resources/allAudio.fxml"));
                            root.setCenter(node);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                playlistsButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        allAudioButton.unSet();
                        playlistsButton.set();
                        settingsButton.unSet();
                        try {
                            Parent node = FXMLLoader.load(getClass().getResource("/resources/playlists.fxml"));
                            root.setCenter(node);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                settingsButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        allAudioButton.unSet();
                        playlistsButton.unSet();
                        settingsButton.set();
                        try {
                            Parent node = FXMLLoader.load(getClass().getResource("/resources/settings.fxml"));
                            root.setCenter(node);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

                allAudioButton.getOnMouseClicked().handle(null);

                imagePlayButton = new Image(getClass().getResourceAsStream("/resources/images/playButton.png"));
                imagePauseButton = new Image(getClass().getResourceAsStream("/resources/images/pauseButton.png"));
                imageNextButton = new Image(getClass().getResourceAsStream("/resources/images/nextButton.png"));
                imagePrevButton = new Image(getClass().getResourceAsStream("/resources/images/prevButton.png"));

                playPauseImageView.setImage(imagePlayButton);
                prevImageView.setImage(imagePrevButton);
                nextImageView.setImage(imageNextButton);

                playPauseButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        player.toggle();
                        if (player.isPlaying()) {
                            playPauseImageView.setImage(imagePauseButton);
                        } else {
                            playPauseImageView.setImage(imagePlayButton);
                        }
                    }
                });

                prevButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        player.prev();
                        int audioID = player.GetCurrentAudioID();
                        ClipData clipData = MusicDatabase.GetClipData(audioID);
                        //clipData
                    }
                });

                nextButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        player.next();
                    }
                });


                controller.init(root);

                MusicPlaylist playlist = new MusicPlaylist();
                playlist.AddToEnd(0);
                playlist.AddToEnd(1);

                player = new MusicPlayer();
                controller.setPlayer(player);
                player.SetOnStateChangeCallback(control);
                player.SetOnNextPrevChangedCallback(control);
                player.SetOnProgressChangeCallback(controller);
                player.SetPlaylist(playlist, 0);
                player.pause();
                player.start();
                player.setVolume(1);

                root.getScene().getWindow().setOnCloseRequest(new EventHandler<WindowEvent>() {
                    @Override
                    public void handle(WindowEvent windowEvent) {
                        player.pause();
                        player.Destroy();
                        System.exit(0);
                    }
                });
            }
        });
    }

    @Override
    public void OnStateChanged(MusicPlayerState currentState) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (currentState == MusicPlayerState.Played) {
                    playPauseImageView.setImage(imagePauseButton);
                } else if (currentState == MusicPlayerState.Paused) {
                    playPauseImageView.setImage(imagePlayButton);
                }
            }
        });
    }

    @Override
    public void OnChanged(MusicPlayer player) {
        MusicSocket socket = NetworkManager.GetMusicSocket();
        String[] data = socket.GetAudioTextData(player.GetCurrentAudioID());
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                authorLabel.setText(data[0]);
                audioNameLabel.setText(data[1]);
            }
        });
    }
}
