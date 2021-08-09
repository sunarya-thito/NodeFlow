package thito.nodeflow.engine.util;

import javafx.animation.*;
import javafx.beans.property.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.stage.*;
import javafx.util.*;
import thito.nodeflow.engine.skin.*;

public class CalloutsPopup extends Popup {
    private BorderPane root = new BorderPane();
//    private Scene scene = new Scene(root);
    private CalloutsSkin skin = new CalloutsSkin();

    private Timeline openAnimation, closeAnimation;
    public CalloutsPopup() {
        getContent().add(root);
        root.setBackground(Background.EMPTY);
        root.setCenter(skin);
        root.setOpacity(0);
        skin.getContent().setMaxHeight(0);
        skin.getContent().setPrefHeight(200);
        closeAnimation = new Timeline(new KeyFrame(Duration.millis(100), new KeyValue(skin.getContent().maxHeightProperty(), 0, Interpolator.EASE_OUT)),
                new KeyFrame(Duration.millis(100), new KeyValue(root.opacityProperty(), 0)));
        closeAnimation.setOnFinished(e -> {
            super.hide();
        });
    }

    public CalloutsSkin getSkin() {
        return skin;
    }

    private Timeline sizeAnimation;
    public void updateSize() {
        if (openAnimation != null) openAnimation.stop();
        if (sizeAnimation != null) sizeAnimation.stop();
        sizeAnimation = new Timeline(new KeyFrame(Duration.millis(150), new KeyValue(skin.getContent().maxHeightProperty(), skin.getContent().prefHeight(skin.getContent().getWidth()), Interpolator.EASE_IN)));
        sizeAnimation.play();
    }

    @Override
    protected void show() {
        super.show();
        closeAnimation.stop();
        if (openAnimation != null) openAnimation.stop();
        openAnimation = new Timeline(new KeyFrame(Duration.millis(100), new KeyValue(root.opacityProperty(), 1)),
                new KeyFrame(Duration.millis(150), new KeyValue(skin.getContent().maxHeightProperty(), skin.getContent().prefHeight(skin.getContent().getWidth()), Interpolator.EASE_IN)));
        openAnimation.play();
    }

    @Override
    public void hide() {
        if (openAnimation != null) openAnimation.stop();
        closeAnimation.play();
    }
}
