package sample.client.view;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import sample.client.model.MusicPlaylist;
import sample.client.model.MusicSocket;
import sample.client.model.NetworkManager;

import java.net.URL;
import java.util.ResourceBundle;

public class AllAudioController implements Initializable {

    @FXML
    Button refreshButton;
    @FXML
    ListView<AudioCellData> containerListView;
    ObservableList<AudioCellData> observableList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                MusicSocket socket = NetworkManager.GetMusicSocket();
                if (socket == null) return;

                int[] audios = socket.GetAllAudio();
                AudioCellData[] cells = new AudioCellData[audios.length];

                for (int i = 0; i < audios.length; i++) {
                    String[] data = socket.GetAudioTextData(audios[i]);
                    cells[i] = new AudioCellData();
                    cells[i].audioID = audios[i];
                    cells[i].author = data[0];
                    cells[i].audioName = data[1];
                }

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {

                        observableList.setAll(cells);
                        containerListView.setItems(observableList);
                        containerListView.setCellFactory(new Callback<ListView<AudioCellData>, ListCell<AudioCellData>>() {
                            @Override
                            public ListCell<AudioCellData> call(ListView<AudioCellData> listView) {
                                AudioCell cell = new AudioCell();
                                cell.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                                    @Override
                                    public void handle(MouseEvent event) {
                                        AudioCell c = (AudioCell) event.getSource();
                                        if(c == null) return;
                                        int index = c.getIndex();
                                        if(index >= audios.length) return;
                                        containerListView.getSelectionModel().getSelectedItem();
                                        MusicPlaylist playlist = new MusicPlaylist();
                                        playlist.AddAll(audios);
                                        UIController.player.SetPlaylist(playlist, index);                                        UIController.player.SetPlaylist(playlist, index);
                                        UIController.player.play();
                                    }
                                });
                                return cell;
                            }
                        });
                    }
                });
            }
        }).start();
    }
}
