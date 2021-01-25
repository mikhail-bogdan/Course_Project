package sample.client;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;


public class PopupTimeView {
    Label label;
    Stage stage;
    long currentSeconds = 0;

    public PopupTimeView(Window window) {
        Paint paint = new Color(1, 1, 1, 1);
        CornerRadii cornerRadii = new CornerRadii(4);
        BackgroundFill fill = new BackgroundFill(paint, cornerRadii, null);
        label = new Label("0:00");
        label.setFont(new Font(14));
        label.setBackground(new Background(fill));
        label.setPadding(new Insets(1));
        Scene scene = new Scene(label);
        scene.setFill(new Color(0, 0, 0, 0));

        stage = new Stage(StageStyle.TRANSPARENT);
        stage.initOwner(window);
        stage.setAlwaysOnTop(true);
        stage.initModality(Modality.NONE);
        stage.setScene(scene);
    }

    public void setPos(double x, double y) {
        stage.setX(x);
        stage.setY(y);
    }

    public void setTime(long seconds) {
        if (seconds == currentSeconds) return;
        currentSeconds = seconds;

        StringBuilder builder = new StringBuilder();
        long minutes = seconds / 60;
        seconds = seconds % 60;
        builder.append(minutes).append(":");
        sprintf0d(builder, (int)seconds, 2);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                label.setText(builder.toString());
            }
        });
    }

    private StringBuilder sprintf0d(StringBuilder sb, int value, int width) {
        long d = value;
        if (d < 0) {
            sb.append('-');
            d = -d;
            --width;
        }
        int n = 10;
        for (int i = 2; i < width; i++) {
            n *= 10;
        }
        for (int i = 1; i < width && d < n; i++) {
            sb.append('0');
            n /= 10;
        }
        sb.append(d);
        return sb;
    }

    public void show() {
        stage.show();
    }

    public void hide() {
        stage.hide();
    }

    public Point2D getSize() {
        return new Point2D(stage.getWidth(), stage.getHeight());
    }
}
