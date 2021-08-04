package thito.nodeflow.internal.ui;

import javafx.beans.property.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.stage.*;

public class SimpleContextMenu {
    private Stage stage;
    private Scene scene;
    private BorderPane pane;
    private DoubleProperty x = new SimpleDoubleProperty();
    private DoubleProperty y = new SimpleDoubleProperty();
    protected boolean center;

    public SimpleContextMenu(WindowImpl owner) {
        initialize(owner.getStage());
    }

    public SimpleContextMenu(Stage owner) {
        initialize(owner);
    }

    private void initialize(Stage owner) {
        pane = new BorderPane();
        stage = new Stage(StageStyle.UNDECORATED);
        stage.setAlwaysOnTop(true);
        stage.initModality(Modality.NONE);
        stage.initOwner(owner);
        scene = new Scene(pane);
        stage.setScene(scene);
        stage.focusedProperty().addListener((obs, old, val) -> {
            if (!val) {
                attemptUnfocusClose();
            }
        });
        x.addListener((obs, old, val) -> {
            updatePosition();
        });
        y.addListener((obs, old, val) -> {
            updatePosition();
        });
        stage.heightProperty().addListener(obs -> updatePosition());
        stage.widthProperty().addListener(obs -> updatePosition());
    }

    public BorderPane getPane() {
        return pane;
    }

    protected void attemptUnfocusClose() {
        close();
    }

    private void updatePosition() {
        stage.setX(x.get());
        if (center) {
            stage.setY(y.get() - pane.getHeight() / 2);
        } else {
            stage.setY(y.get());
        }
    }

    public void close() {
        stage.close();
    }

    public void setX(double x) {
        this.x.set(x);
    }

    public void setY(double y) {
        this.y.set(y);
    }

    public void show() {
        this.stage.show();
    }

    public void hide() {
        this.stage.hide();
    }

    public boolean isShowing() {
        return this.stage.isShowing();
    }

    public void show(double x, double y) {
        this.x.set(x);
        this.y.set(y);
        stage.show();
    }

    protected void setViewport(Node node) {
        pane.setCenter(node);
    }

    public Stage getStage() {
        return stage;
    }
}
