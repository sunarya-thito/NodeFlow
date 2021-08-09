package thito.nodeflow.library.ui.decoration.window;

import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.effect.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.stage.*;
import thito.nodeflow.internal.*;
import thito.nodeflow.internal.ui.*;

public class WindowBase {

    private static final int maximizedWindowFrameThickness = 10;
    private static final int frameBorderThickness = 2;
    protected final BorderPane content = new BorderPane();
    protected final StackPane root = new StackPane(content);

    private WindowBar bar;
    private WindowViewport viewport;

    protected final Stage stage = new Stage(StageStyle.TRANSPARENT);
    protected final Scene scene = new Scene(root, 800, 600);

    public WindowBase() {
        stage.getProperties().put(WindowBase.class, this);
        ColorAdjust effect = new ColorAdjust(0, 0, 0, 0);
        effect.setInput(new GaussianBlur(0));
        root.setEffect(effect);
        Toolkit.style(root, "window-root");
        Toolkit.style(content, "window-content");
        Rectangle rectangle = new Rectangle();
        rectangle.setY(StaticMenuBar.HEIGHT);
        rectangle.setArcHeight(15);
        rectangle.setArcWidth(15);
        rectangle.widthProperty().bind(root.widthProperty());
        rectangle.heightProperty().bind(root.heightProperty().subtract(rectangle.yProperty()));
        root.setClip(rectangle);
        StackPane glass = new StackPane();
        glass.setMouseTransparent(true);
        Toolkit.style(glass, "window-glass");
        root.getChildren().add(glass);
        stage.setScene(scene);
        scene.setFill(Color.TRANSPARENT);
        bar = new WindowBar(this);
        viewport = new WindowViewport();
        content.setTop(bar.getPane());
        content.setCenter(viewport.getPane());

        stage.maximizedProperty().addListener((obs, old, val) -> {
            if (val) {
                int padding = maximizedWindowFrameThickness;
                int offset = frameBorderThickness;
                root.setPadding(new Insets(padding - offset + StaticMenuBar.HEIGHT, padding - offset, padding - offset, padding - offset));
            } else {
                root.setPadding(new Insets(StaticMenuBar.HEIGHT, 0, 0, 0));
            }
        });

        stage.addEventHandler(WindowEvent.WINDOW_SHOWN, event -> {
            Toolkit.centerOnScreen(stage);
        });

        root.setPadding(new Insets(StaticMenuBar.HEIGHT, 0, 0, 0));
    }

    public Stage getStage() {
        return stage;
    }

    public WindowBar getBar() {
        return bar;
    }

    public WindowViewport getDisplayViewport() {
        return viewport;
    }

}
