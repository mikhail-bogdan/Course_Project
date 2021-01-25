package sample.client.view;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.css.PseudoClass;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.skin.LabeledSkinBase;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

public class MyButton extends ButtonBase {

    private ImageView imageView;
    private final Image notSetImage = new Image(getClass().getResourceAsStream("/resources/images/notSetButton.png"));
    private final Image setImage = new Image(getClass().getResourceAsStream("/resources/images/setButton.png"));

    private boolean isSet = false;


    public MyButton() {
        super("", new ImageView());
        initialize();
    }

    public MyButton(String text) {
        super(text, new ImageView());
        initialize();
    }

    public MyButton(String text, Node graphics) {
        super(text, graphics);
        initialize();
    }

    private void initialize() {
        imageView = (ImageView) getGraphic();
        imageView.setImage(notSetImage);
        imageView.setTranslateX(-39);
        getChildren().add(imageView);
        setBackground(buttonNormalColor);
        this.setOnMouseEntered(mouseEntered);
        this.setOnMouseExited(mouseExited);
        this.setOnMousePressed(mousePressed);
        this.setOnMouseReleased(mouseReleased);
    }

    public void set() {
        Platform.runLater(() -> imageView.setImage(setImage));
        isSet = true;
    }

    public void unSet() {
        Platform.runLater(() -> imageView.setImage(notSetImage));
        isSet = false;
    }


    @Override
    public void fire() {

    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new LabeledSkinBase<MyButton>(this) {
            
        };
    }

    private BooleanProperty setButton;
    public final void setSetButton(boolean value) {
        setButtonProperty().set(value);
    }
    public final boolean isSetButton() {
        return setButton == null ? false : setButton.get();
    }

    public final BooleanProperty setButtonProperty() {
        if (setButton == null) {
            setButton = new BooleanPropertyBase(false) {
                @Override protected void invalidated() {
                    isSet = get();
                }

                @Override
                public Object getBean() {
                    return MyButton.this;
                }

                @Override
                public String getName() {
                    return "setButton";
                }
            };
        }
        return setButton;
    }

    private BooleanProperty defaultButton;
    public final void setDefaultButton(boolean value) {
        defaultButtonProperty().set(value);
    }
    public final boolean isDefaultButton() {
        return defaultButton == null ? false : defaultButton.get();
    }

    public final BooleanProperty defaultButtonProperty() {
        if (defaultButton == null) {
            defaultButton = new BooleanPropertyBase(false) {
                @Override protected void invalidated() {
                    pseudoClassStateChanged(PSEUDO_CLASS_DEFAULT, get());
                }

                @Override
                public Object getBean() {
                    return MyButton.this;
                }

                @Override
                public String getName() {
                    return "defaultButton";
                }
            };
        }
        return defaultButton;
    }

    private BooleanProperty cancelButton;
    public final void setCancelButton(boolean value) {
        cancelButtonProperty().set(value);
    }
    public final boolean isCancelButton() {
        return cancelButton == null ? false : cancelButton.get();
    }

    public final BooleanProperty cancelButtonProperty() {
        if (cancelButton == null) {
            cancelButton = new BooleanPropertyBase(false) {
                @Override protected void invalidated() {
                    pseudoClassStateChanged(PSEUDO_CLASS_CANCEL, get());
                }

                @Override
                public Object getBean() {
                    return MyButton.this;
                }

                @Override
                public String getName() {
                    return "cancelButton";
                }
            };
        }
        return cancelButton;
    }

    private static final String DEFAULT_STYLE_CLASS = "button";
    private static final PseudoClass PSEUDO_CLASS_DEFAULT
            = PseudoClass.getPseudoClass("default");
    private static final PseudoClass PSEUDO_CLASS_CANCEL
            = PseudoClass.getPseudoClass("cancel");

    private final Background buttonTintColor = new Background(new BackgroundFill(new Color(190.0 / 255, 190.0 / 255, 190.0 / 255, 1), new CornerRadii(0), null));
    private final Background buttonNormalColor = new Background(new BackgroundFill(new Color(220.0 / 255, 220.0 / 255, 220.0 / 255, 1), new CornerRadii(0), null));
    private final Background buttonClickColor = new Background(new BackgroundFill(new Color(150.0 / 255, 150.0 / 255, 150.0 / 255, 1), new CornerRadii(0), null));


    private final EventHandler<MouseEvent> mouseEntered = mouseEvent -> {
        MyButton button = (MyButton) mouseEvent.getSource();
        if(button == null) return;
        if(button.isPressed()) return;
        button.setBackground(buttonTintColor);
    };
    private final EventHandler<MouseEvent> mouseExited = mouseEvent ->  {
        MyButton button = (MyButton)mouseEvent.getSource();
        if(button == null) return;
        if(button.isPressed()) return;
        button.setBackground(buttonNormalColor);
    };
    private final EventHandler<MouseEvent> mousePressed = mouseEvent -> {
        MyButton button = (MyButton)mouseEvent.getSource();
        if(button == null) return;
        button.setBackground(buttonClickColor);
    };
    private final EventHandler<MouseEvent> mouseReleased = mouseEvent -> {
        MyButton button = (MyButton)mouseEvent.getSource();
        if(button == null) return;
        if(button.isHover()) {
            button.setBackground(buttonTintColor);
        } else {
            button.setBackground(buttonNormalColor);
        }
    };
}
