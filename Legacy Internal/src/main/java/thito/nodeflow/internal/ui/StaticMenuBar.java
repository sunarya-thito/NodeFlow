package thito.nodeflow.internal.ui;

import javafx.beans.*;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import thito.nodeflow.api.task.*;
import thito.nodeflow.api.ui.Window;
import thito.nodeflow.internal.*;
import thito.nodeflow.internal.ui.popup.*;
import thito.nodeflow.internal.ui.resourcemonitor.*;
import thito.nodeflow.library.ui.*;

import java.math.*;

public class StaticMenuBar implements Tickable {

    public static int HEIGHT = 24;

    private Screen screen;
    private Stage stage;
    private HBox box;
    private Label memory;

    public StaticMenuBar(Screen screen, Stage owner) {
        this.screen = screen;
        initialize(owner);
    }

    private void initialize(Stage owner) {
        stage = new StaticBarStage();
        stage.initOwner(owner);
        stage.setResizable(false);
        stage.setX(screen.getVisualBounds().getMinX());
        stage.setY(screen.getVisualBounds().getMinY());
        stage.setWidth(screen.getVisualBounds().getMaxX() - stage.getX());
        stage.setHeight(HEIGHT);
        box = new HBox();

//        {
//            // Icon
//            ImageView view = new ImageView("icons/favicon.png");
//            Toolkit.style(view, "above-bar-favicon");
//            box.getChildren().add(view);
//        }

        {
            // Title
            Label label = new Label("NF");
            label.setStyle("-fx-font-family: \"AXIS Extra Bold\"; -fx-font-size: 15; -fx-text-fill: white;");
            box.getChildren().add(label);
        }

        {
            // Title
            Label label = new Label("NodeFlow");
            Toolkit.style(label, "above-bar-title");
            box.getChildren().add(label);
        }

        Window window = (Window) owner.getProperties().get(Window.class);
        if (window != null) {
            box.getChildren().add(window.getMenu().impl_createPeer());
        }

        {
            // Left Bar
            HBox lefty = new HBox();
            HBox.setHgrow(lefty, Priority.ALWAYS);
            lefty.setAlignment(Pos.CENTER_RIGHT);
            memory = new Label();
            memory.setOnMouseClicked(event -> {
                if (event.getClickCount() >= 2) {
                    ResourceMonitorWindow win = UIManagerImpl.getInstance().getWindowsManager().getResourceMonitor();
                    win.show();
                    win.impl_getPeer().toFront();
                }
                AppStatsPopup popup = new AppStatsPopup(stage);
                popup.getStage().show();
                popup.getStage().requestFocus();
                popup.getStage().focusedProperty().addListener((obs, old, val) -> {
                    if (!val) {
                        popup.getStage().close();
                    }
                });
                popup.getStage().setY(screen.getVisualBounds().getMinY() + HEIGHT);
                InvalidationListener listener = x -> {
                    popup.getStage().setX(screen.getVisualBounds().getMaxX() - popup.getStage().getWidth());
                };
                popup.getStage().widthProperty().addListener(listener);
                listener.invalidated(null);
            });
            Toolkit.style(memory, "memory");
            lefty.getChildren().addAll(memory);
            box.getChildren().add(lefty);
        }

        box.setAlignment(Pos.CENTER_LEFT);

        Toolkit.style(box, "above-bar");
        Scene scene = new Scene(box);
        stage.setScene(scene);
        stage.setAlwaysOnTop(false);
        stage.setOnCloseRequest(event -> {
            event.consume();
            owner.fireEvent(new WindowEvent(owner, WindowEvent.WINDOW_CLOSE_REQUEST));
        });
        owner.addEventHandler(WindowEvent.WINDOW_HIDDEN, event -> {
            stage.hide();
        });
        owner.addEventHandler(WindowEvent.WINDOW_SHOWN, event -> {
            stage.show();
        });
        Ticker.register(this);
    }

    @Override
    public void tick() {
        Runtime runtime = Runtime.getRuntime();
        memory.setText(displayByteSize(runtime.totalMemory() - runtime.freeMemory())+" / "+ displayByteSize(runtime.totalMemory()));
    }

    public static String displayByteSize(long bytes) {
        return Toolkit.byteCountToDisplaySize(new BigInteger(String.valueOf(bytes)));
    }

    public void show() {
        if (!stage.isShowing()) {
            Task.runOnForeground("StaticMenuBar-show", () -> {
                stage.show();
            });
        }
    }

    public void destroy() {
        if (stage.isShowing()) {
            Task.runOnForeground("StaticMenuBar-hide", () -> {
                stage.close();
            });
        }
    }

    public void toBack() {
        if (stage.isShowing()) {
            if (!stage.isFocused()) {
                stage.toBack();
            }
        }
    }

    public void toFront() {
        if (stage.isShowing()) {
            stage.toFront();
        }
    }

    public static class StaticBarStage extends Stage {
        public StaticBarStage() {
            super(StageStyle.TRANSPARENT);
        }
    }
}
