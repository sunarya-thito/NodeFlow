package thito.nodeflow.library.ui;

import javafx.animation.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.effect.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.stage.*;
import javafx.util.*;
import thito.nodeflow.internal.*;
import thito.nodeflow.platform.*;

import java.util.concurrent.atomic.*;

public class ImageViewTooltip {
    private Stage tooltip;

    public ImageViewTooltip(BetterImageView biv) {
        tooltip = new Stage(StageStyle.TRANSPARENT);
        NativeToolkit.TOOLKIT.makeFullyTransparent(tooltip);
        AtomicInteger time = new AtomicInteger();
        Timeline[] atomic = new Timeline[1];
        atomic[0] = new Timeline(new KeyFrame(Duration.millis(16), action -> {
            double mouseX = Toolkit.getMouseX();
            double mouseY = Toolkit.getMouseY();
            int tick = time.getAndIncrement();
            if (tick == 0) {
                ImageView view = new ImageView();
                view.setSmooth(false);
                BorderPane shadow = new BorderPane(view);
                shadow.setEffect(new DropShadow(20, Color.color(0, 0, 0, 0.6)));
                shadow.getStyleClass().add("image-view-tooltip");
                BorderPane pane = new BorderPane(shadow);
                pane.setBackground(Background.EMPTY);
                pane.setPadding(new Insets(25));
                view.setPreserveRatio(true);
                view.setFitHeight(120);
                view.setFitWidth(120);
                view.imageProperty().bind(biv.imageProperty());
                tooltip.setScene(new Scene(pane));
                tooltip.getScene().setFill(Color.TRANSPARENT);
                tooltip.initOwner(biv.getScene().getWindow());
            }
            tooltip.setX(mouseX - 13);
            tooltip.setY(mouseY - 13);
            if (tick == 1) {
                tooltip.show();
            }
            if (tick > 200) {
                tooltip.hide();
                tooltip = new Stage(StageStyle.TRANSPARENT);
                NativeToolkit.TOOLKIT.makeFullyTransparent(tooltip);
                atomic[0].stop();
            }
        }));
        atomic[0].setCycleCount(Animation.INDEFINITE);
        biv.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
            time.set(0);
            atomic[0].play();
        });
        biv.addEventHandler(MouseEvent.MOUSE_EXITED, event -> {
            tooltip.hide();
            tooltip = new Stage(StageStyle.TRANSPARENT);
            NativeToolkit.TOOLKIT.makeFullyTransparent(tooltip);
            atomic[0].stop();
        });
    }

}
