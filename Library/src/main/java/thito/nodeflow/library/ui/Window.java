package thito.nodeflow.library.ui;

import com.sun.javafx.css.*;
import javafx.application.*;
import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.css.*;
import javafx.scene.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.stage.*;
import thito.nodeflow.library.application.*;
import thito.nodeflow.library.platform.*;

public abstract class Window {
    public static final PseudoClass MAXIMIZED = PseudoClass.getPseudoClass("maximized");
    protected Stage stage = new Stage();
    private WindowHitTest windowHitTest;
    private ObjectProperty<Skin> skin = new SimpleObjectProperty<>();
    private LayoutDebugger debugger;
    private StackPane root;

    public Window() {
        initializeWindow();
    }

    protected abstract Skin createSkin();
    protected abstract WindowHitTest createHitTest();

    protected void initializeWindow() {
        skin.addListener((obs, old, val) -> {
            if (old != null) root.getChildren().remove(old);
            if (val != null) {
                root.getChildren().add(0, val);
            }
        });

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

        Scene scene = new Scene(root, -1, -1, false, SceneAntialiasing.BALANCED);
        scene.setFill(Color.TRANSPARENT);
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

        root.layoutBoundsProperty().addListener(obs -> {
            stage.setMinWidth(root.minWidth(root.getHeight()));
            stage.setMinHeight(root.minHeight(root.getWidth()));
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