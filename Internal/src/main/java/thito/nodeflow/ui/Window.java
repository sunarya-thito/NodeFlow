package thito.nodeflow.ui;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.property.*;
import javafx.css.PseudoClass;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import thito.nodeflow.binding.ThreadBinding;
import thito.nodeflow.platform.NativeToolkit;
import thito.nodeflow.task.TaskThread;
import thito.nodeflow.task.batch.Progress;
import thito.nodeflow.ui.task.ProgressSkin;

public abstract class Window {
    public static final PseudoClass MAXIMIZED = PseudoClass.getPseudoClass("maximized");
    protected Stage stage = new Stage();
    private WindowHitTest windowHitTest;
    private ObjectProperty<Skin> skin = new SimpleObjectProperty<>();
    private LayoutDebugger debugger;
    private StackPane root;
    private StringProperty title = new SimpleStringProperty();

    private ObjectProperty<Progress> progress = new SimpleObjectProperty<>();
    private BorderPane backgroundOverlay = new BorderPane();

    public ObjectProperty<Progress> progressProperty() {
        return progress;
    }

    public Window() {
        initializeWindow();
    }

    protected abstract Skin createSkin();
    protected abstract WindowHitTest createHitTest();

    protected StringProperty titleProperty() {
        return title;
    }

    protected void initializeWindow() {
        backgroundOverlay.getStyleClass().add("window-background-task-overlay");
        backgroundOverlay.addEventFilter(EventType.ROOT, Event::consume);
        backgroundOverlay.visibleProperty().bind(progressProperty().isNotNull());
        backgroundOverlay.mouseTransparentProperty().bind(progressProperty().isNull());
        progressProperty().addListener((obs, old, val) -> {
            backgroundOverlay.setCenter(null);
            if (val != null) {
                ProgressSkin progressSkin = new ProgressSkin();
                ThreadBinding.bind(progressSkin.getStatus().textProperty(), val.statusProperty(), TaskThread.BG(), TaskThread.UI());
                ThreadBinding.bind(progressSkin.getProgressBar().progressProperty(), val.progressProperty(), TaskThread.BG(), TaskThread.UI());
                backgroundOverlay.setCenter(progressSkin);
            }
        });

        skin.addListener((obs, old, val) -> {
            if (old != null) {
                old.getChildren().remove(backgroundOverlay);
                root.getChildren().remove(old);
            }
            if (val != null) {
                val.getChildren().add(backgroundOverlay);
                root.getChildren().add(0, val);
            }
        });

        stage.getIcons().add(new Image("rsrc:Images/SplashedLogo.png"));
        stage.getProperties().put(Window.class, this);

        windowHitTest = createHitTest();

        NativeToolkit.TOOLKIT.registerNativeWindowHandling(this);

        root = new StackPane();
        debugger = new LayoutDebugger(this);
        root.getChildren().add(debugger.getHighlightLayer());
        root.setBackground(Background.EMPTY);

        stage.maximizedProperty().addListener((obs, old, val) -> {
            skin.get().pseudoClassStateChanged(MAXIMIZED, val);
        });

        InvalidationListener update = obs -> {
            stage.setMinWidth(root.minWidth(-1));
            stage.setMinHeight(root.minHeight(-1));
        };

        skin.addListener((obs, old, val) -> {
            if (old != null) val.layoutBoundsProperty().removeListener(update);
            if (val != null) val.layoutBoundsProperty().addListener(update);
        });

        Scene scene = new Scene(root, -1, -1, false, SceneAntialiasing.BALANCED);
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add("theme:StyleSheets/thito/nodeflow/ui/Skin.css");
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.F5) {
                System.out.println("RELOAD");
                skin.get().reload();
            } else if (event.getCode() == KeyCode.F6) {
                debugger.visibleProperty().set(!debugger.visibleProperty().get());
            } else if (event.getCode() == KeyCode.F7) {
                debugger.lockObjectProperty().set(!debugger.isLockObject());
            } else if (event.getCode() == KeyCode.F8) {
                System.out.println("CSS RELOAD");
                skin.get().reloadCSS();
            }
        });
        stage.setScene(scene);
        skin.set(createSkin());

        ThreadBinding.bind(stage.titleProperty(), titleProperty(), TaskThread.BG(), TaskThread.UI());

        stage.setOnCloseRequest(event -> {
            event.consume();
            close();
        });
    }

    public StackPane getRoot() {
        return root;
    }

    public Skin getSkin() {
        return skin.get();
    }

    public void setSkin(Skin skin) {
        this.skin.set(skin);
    }

    public ObjectProperty<Skin> skinProperty() {
        return skin;
    }

    public void show() {
        Platform.runLater(() -> {
            stage.show();
            stage.toFront();
            stage.sizeToScene();
            stage.centerOnScreen();
        });
    }

    public void close() {
        stage.close();
    }

    public void setIconified(boolean b) {
        stage.setIconified(b);
    }

    public boolean isIconified() {
        return stage.isIconified();
    }

    public ReadOnlyBooleanProperty iconifiedProperty() {
        return stage.iconifiedProperty();
    }

    public void setMaximized(boolean b) {
        stage.setMaximized(b);
    }

    public boolean isMaximized() {
        return stage.isMaximized();
    }

    public ReadOnlyBooleanProperty maximizedProperty() {
        return stage.maximizedProperty();
    }

    public Stage getStage() {
        return stage;
    }

    public WindowHitTest getWindowHitTest() {
        return windowHitTest;
    }
}
