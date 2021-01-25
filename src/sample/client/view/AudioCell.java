package sample.client.view;

import javafx.scene.control.ListCell;

public class AudioCell extends ListCell<AudioCellData> {

    int audioID;
    Data data;

    public AudioCell() {

    }

    @Override
    protected void updateItem(AudioCellData item, boolean empty) {
        super.updateItem(item, empty);
        if(item != null)
        {
            audioID = item.audioID;
            data = new Data();
            data.setInfo(item.author, item.audioName);
            setGraphic(data.getBox());
        }
    }
}
