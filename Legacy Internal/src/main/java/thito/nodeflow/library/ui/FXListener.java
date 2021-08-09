package thito.nodeflow.library.ui;

import com.sun.javafx.stage.*;
import javafx.beans.value.*;
import javafx.collections.*;
import javafx.event.*;
import javafx.scene.*;
import javafx.stage.*;

public class FXListener implements ListChangeListener<Window> {

    private EventHandler<WindowEvent> shownListener = event -> onStageShown((Window) event.getSource());
    private EventHandler<WindowEvent> hiddenListener = event -> onStageHidden((Window) event.getSource());
    private ChangeListener<Scene> stageListener = (obs, oldValue, newValue) -> {
        if (oldValue != null) {
            removeSceneListener(oldValue);
        }
        if (newValue != null) {
            addSceneListener(newValue);
            newValue.addEventHandler(WindowEvent.WINDOW_SHOWN, shownListener);
        }
    };
    private ChangeListener<Parent> sceneListener = (obs, oldValue, newValue) -> {
        if (oldValue != null) {
            removeNodeListener(oldValue);
        }
        if (newValue != null) {
            addNodeListener(newValue);
        }
    };
    private ListChangeListener<Node> nodeListener = c -> {
        while (c.next()) {
            for (Node node : c.getRemoved()) {
                removeNodeListener(node);
            }
            for (Node node : c.getAddedSubList()) {
                addNodeListener(node);
            }
        }
    };

    public FXListener() {
        ObservableList<? extends Window> stages = StageHelper.getStages();
        stages.addListener(this);
        for (Window stage : stages) {
            addStageListener(stage);
            onStageAdded(stage);
        }
    }

    public void onNodeAdded(Node node) {
    }

    public void onNodeRemoved(Node node) {
    }

    public void onSceneAdded(Scene scene) {
    }

    public void onSceneRemoved(Scene scene) {
    }

    public void onStageAdded(Window stage) {
    }

    public void onStageRemoved(Window stage) {
    }

    public void onStageShown(Window stage) {

    }

    public void onStageHidden(Window stage) {

    }

    private void addStageListener(Window stage) {
        onStageAdded(stage);
        Scene current = stage.getScene();
        if (current != null) {
            addSceneListener(current);
        }
        stage.addEventHandler(WindowEvent.WINDOW_SHOWN, shownListener);
        stage.addEventHandler(WindowEvent.WINDOW_HIDDEN, hiddenListener);
        stage.sceneProperty().addListener(stageListener);
    }

    private void removeStageListener(Window stage) {
        onStageRemoved(stage);
        stage.sceneProperty().removeListener(stageListener);
        Scene current = stage.getScene();
        stage.removeEventHandler(WindowEvent.WINDOW_SHOWN, shownListener);
        stage.removeEventHandler(WindowEvent.WINDOW_HIDDEN, hiddenListener);
        if (current != null) {
            removeSceneListener(current);
        }
    }

    private void addSceneListener(Scene scene) {
        onSceneAdded(scene);
        Parent root = scene.getRoot();
        if (root != null) {
            addNodeListener(root);
        }
        scene.rootProperty().addListener(sceneListener);
    }

    private void removeSceneListener(Scene scene) {
        onSceneRemoved(scene);
        scene.rootProperty().removeListener(sceneListener);
        Parent root = scene.getRoot();
        if (root != null) {
            removeNodeListener(root);
        }
    }

    private void addNodeListener(Node node) {
        onNodeAdded(node);
        if (node instanceof Parent) {
            for (Node child : ((Parent) node).getChildrenUnmodifiable()) {
                addNodeListener(child);
            }
            ((Parent) node).getChildrenUnmodifiable().addListener(nodeListener);
        }
    }

    private void removeNodeListener(Node node) {
        onNodeRemoved(node);
        if (node instanceof Parent) {
            ((Parent) node).getChildrenUnmodifiable().removeListener(nodeListener);
            for (Node child : ((Parent) node).getChildrenUnmodifiable()) {
                removeNodeListener(child);
            }
        }
    }

    @Override
    public void onChanged(Change<? extends Window> c) {
        while (c.next()) {
            for (Window removed : c.getRemoved()) {
                removeStageListener(removed);
            }
            for (Window added : c.getAddedSubList()) {
                addStageListener(added);
            }
        }
    }

}
