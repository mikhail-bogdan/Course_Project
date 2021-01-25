package sample.client.view;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Window;
import sample.client.model.MusicPlayer;
import sample.client.model.OnProgressChangeEvent;

import java.io.IOException;

public class MusicHorizontalBarController extends VBox implements OnProgressChangeEvent {
    @FXML
    private Canvas canvas;
    private boolean mouseDragged = false;
    private MusicPlayer player;
    PopupTimeView endTimeTooltip;
    PopupTimeView currentTimeTooltip;
    PopupTimeView setTimeTooltip;

    public OnProgressChangeEvent onProgressChangeEvent;

    public MusicHorizontalBarController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/resources/MusicHorizontalBar.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                double x = event.getX();
                setProgress(x / canvas.getWidth());
                mouseDragged = true;
            }
        });

        setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                double x = event.getX();
                player.SetProgress(Math.min(1, Math.max(0, x / canvas.getWidth())));
                mouseDragged = false;
            }
        });

        setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                Point2D point = localToScreen(getLayoutBounds().getMinX(), getLayoutBounds().getMinY());
                endTimeTooltip.setPos(point.getX() + canvas.getWidth() - endTimeTooltip.getSize().getX(), point.getY() - endTimeTooltip.getSize().getY());
                currentTimeTooltip.setPos(point.getX(), point.getY() - currentTimeTooltip.getSize().getY());
                endTimeTooltip.show();
                currentTimeTooltip.show();
            }
        });

        setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                endTimeTooltip.hide();
                currentTimeTooltip.hide();
            }
        });

        setProgress(0);
    }


    public void init(Parent parent) {
        Window window = parent.getScene().getWindow();
        endTimeTooltip = new PopupTimeView(window);
        currentTimeTooltip = new PopupTimeView(window);
        setTimeTooltip = new PopupTimeView(window);
        canvas.widthProperty().bind(((BorderPane)parent).widthProperty());
        Point2D point = localToScreen(getLayoutBounds().getMinX(), getLayoutBounds().getMinY());
        endTimeTooltip.setPos(point.getX() + canvas.getWidth() - endTimeTooltip.getSize().getX(), point.getY() - endTimeTooltip.getSize().getY());
        currentTimeTooltip.setPos(point.getX(), point.getY() - currentTimeTooltip.getSize().getY());
    }

    public void setPlayer(MusicPlayer player) {
        this.player = player;
    }

    public void SetProgress(long currentFrame, long totalFrames) {
        if (currentTimeTooltip != null)
            currentTimeTooltip.setTime(currentFrame / 44100);
        if (endTimeTooltip != null)
            endTimeTooltip.setTime(totalFrames / 44100);
        if (mouseDragged) return;
        setProgress((double) currentFrame / totalFrames);
    }

    private volatile boolean isLaterDone = true;

    private void setProgress(double progress) {
        if (!isLaterDone) return;
        isLaterDone = false;
        final Color color = new Color(150d / 255d, 150d / 255d, 150d / 255d, 1d);
        final Color color2 = new Color(1d, 240d / 255d, 0d, 1d);
        final double width = canvas.getWidth() * progress;
        final GraphicsContext context = canvas.getGraphicsContext2D();

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                context.setLineWidth(0);
                context.setFill(color);
                context.fill();
                context.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
                context.setFill(color2);
                context.fillRect(0, 0, width, canvas.getHeight());
                isLaterDone = true;
            }
        });
    }

    @Override
    public void OnProgressChange(long currentFrame, long totalFrames) {
        SetProgress(currentFrame, totalFrames);
    }


}
