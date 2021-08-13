package thito.nodeflow.library.ui;

import javafx.beans.*;
import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.collections.*;
import javafx.scene.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import thito.nodeflow.library.platform.*;

import java.util.*;

public class Window {
    protected Stage stage = new Stage();
    private WindowTitleBarInfo titleBarInfo = new WindowTitleBarInfo();
    private ObjectProperty<Skin> skin = new SimpleObjectProperty<>();
    private LayoutDebugger debugger;

    public Window() {
        initializeWindow();
    }

    protected Skin createSkin() {
        throw new UnsupportedOperationException();
    }

    protected void initializeWindow() {
        skin.set(createSkin());

        NativeToolkit.TOOLKIT.makeBorderless(this);

        debugger = new LayoutDebugger(this);

        BorderPane borderPane = new BorderPane();
        StackPane root = new StackPane(borderPane, debugger.getHighlightLayer());

        Scene scene = new Scene(root);
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.F5) {
                // refresh
                String s = skin.get().getStyleSheet().layoutProperty().get();
                String layout = s.endsWith(" ") ? s.substring(0, s.length() - 1) : s + " ";
                skin.get().getStyleSheet().layoutProperty().set(layout);
                ObservableList<String> cssFiles = skin.get().getStyleSheet().getCssFiles();
                List<String> files = new ArrayList<>(cssFiles);
                cssFiles.clear();
                cssFiles.addAll(files);
            } else if (event.getCode() == KeyCode.F6) {
                debugger.visibleProperty().set(!debugger.visibleProperty().get());
            }
        });
        stage.setScene(scene);

        final ChangeListener<Skin> skinUpdateListener = (obs, old, val) -> {
            borderPane.centerProperty().unbind();
            if (val != null) {
                borderPane.centerProperty().bind(val.rootProperty());
            }
        };

        skin.addListener(skinUpdateListener);
    }

    public ObjectProperty<Skin> skinProperty() {
        return skin;
    }

    public void show() {
        stage.show();
    }

    public void hide() {
        stage.hide();
    }

    public Stage getStage() {
        return stage;
    }

    public WindowTitleBarInfo getTitleBarInfo() {
        return titleBarInfo;
    }
}
