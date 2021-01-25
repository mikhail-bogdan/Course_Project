package sample.client;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class Data
{
    @FXML
    private HBox hBox;
    @FXML
    private Label audioNameLabel;
    @FXML
    private Label authorLabel;

    public Data()
    {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/resources/audioCell.fxml"));
        fxmlLoader.setController(this);
        try
        {
            fxmlLoader.load();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void setInfo(String author, String audioName)
    {
        authorLabel.setText(author);
        audioNameLabel.setText(audioName);
    }

    public HBox getBox()
    {
        return hBox;
    }
}