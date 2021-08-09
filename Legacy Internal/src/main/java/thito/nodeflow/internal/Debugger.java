package thito.nodeflow.internal;

import javafx.animation.*;
import javafx.beans.value.*;
import javafx.collections.*;
import javafx.event.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.stage.*;
import javafx.util.*;
import thito.nodeflow.api.*;

import java.util.logging.*;

public interface Debugger {
    Object HIGHLIGHTER = new Object();
    static boolean isHighlighted(Node root) {
        return root.getProperties().containsKey(HIGHLIGHTER);
    }
    static void printLayout(Node node, int tab) {
        for (int i = 0; i < tab; i++) {
            System.out.print('\t');
        }
        System.out.println(node);
        if (node instanceof Parent) {
            ((Parent) node).getChildrenUnmodifiable().forEach((child) -> printLayout(child, tab + 1));
        }
    }
    static void highlight(Node root) {
        unhighlight(root);
        LayoutDebugger debugger = new LayoutDebugger(root);
        root.getProperties().put(HIGHLIGHTER, debugger);
        debugger.register();
        if (root instanceof Parent) {
            for (Node child : ((Parent) root).getChildrenUnmodifiable()) {
                highlight(child);
            }
        }
    }
    static void unhighlight(Node root) {
        LayoutDebugger.tooltip.highlighted.remove(root);
        Object highlighter = root.getProperties().remove(HIGHLIGHTER);
        if (highlighter instanceof LayoutDebugger) {
            ((LayoutDebugger) highlighter).unregister();
        }
        if (root instanceof Parent) {
            for (Node child : ((Parent) root).getChildrenUnmodifiable()) {
                unhighlight(child);
            }
        }
    }
    static void enableDebugger(Stage stage) {
        disableDebugger(stage);
        StageDebugger debugger = new StageDebugger(stage);
        stage.getProperties().put(StageDebugger.STAGE_DEBUGGER, debugger);
        debugger.register();
    }

    static void disableDebugger(Stage stage) {
        Object debugger = stage.getProperties().remove(StageDebugger.STAGE_DEBUGGER);
        if (debugger instanceof StageDebugger) {
            ((StageDebugger) debugger).unregister();
        }
    }
    class StageDebugger implements ChangeListener<Scene> {
        private static final Object STAGE_DEBUGGER = new Object();
        private static final Object SCENE_DEBUGGER = new Object();
        private Stage stage;
        public StageDebugger(Stage stage) {
            this.stage = stage;
        }

        public void register() {
            if (stage.getScene() != null) {
                setScene(stage.getScene());
            }
            stage.sceneProperty().addListener(this);
        }

        public void unregister() {
            stage.sceneProperty().removeListener(this);
        }

        private void unsetScene(Scene scene) {
            Object debugger = scene.getProperties().remove(SCENE_DEBUGGER);
            if (debugger instanceof SceneDebugger) {
                ((SceneDebugger) debugger).unregister();
            }
        }

        private void setScene(Scene scene) {
            unsetScene(scene);
            SceneDebugger debugger = new SceneDebugger(scene);
            scene.getProperties().put(SCENE_DEBUGGER, debugger);
            debugger.register();
        }

        @Override
        public void changed(ObservableValue<? extends Scene> observable, Scene oldValue, Scene newValue) {
            if (oldValue != null) {
                unsetScene(oldValue);
            }
            if (newValue != null) {
                setScene(newValue);
            }
        }

    }

    class SceneDebugger implements ChangeListener<Node>, EventHandler<KeyEvent> {

        private Scene scene;

        public SceneDebugger(Scene scene) {
            this.scene = scene;
        }

        public void register() {
            scene.rootProperty().addListener(this);
            scene.addEventHandler(KeyEvent.KEY_PRESSED, this);
        }

        public void unregister() {
            scene.rootProperty().removeListener(this);
            scene.removeEventHandler(KeyEvent.KEY_PRESSED, this);
        }

        @Override
        public void handle(KeyEvent event) {
            if (event.getCode() == KeyCode.F12 && event.isControlDown() && event.isAltDown()) {
                Node root = scene.getRoot();
                if (isHighlighted(root)) {
                    NodeFlow.getMainLogger().log(Level.INFO, "Layout Debugger disabled!");
                    unhighlight(root);
                } else {
                    highlight(root);
                    NodeFlow.getMainLogger().log(Level.INFO, "Layout Debugger enabled!");
                }
            }
        }

        @Override
        public void changed(ObservableValue<? extends Node> observable, Node oldValue, Node newValue) {
            boolean wasHighlighted = false;
            if (oldValue != null) {
                wasHighlighted = isHighlighted(oldValue);
                if (wasHighlighted) {
                    unhighlight(oldValue);
                }
            }
            if (newValue != null) {
                if (wasHighlighted) {
                    highlight(newValue);
                }
            }
        }
    }

    class LayoutTooltip implements ListChangeListener<Node>, EventHandler<ActionEvent> {
        private ObservableList<Node> highlighted = FXCollections.observableArrayList();
        private Tooltip tooltip = new Tooltip();
        private Timeline ticker = new Timeline(new KeyFrame(Duration.millis(16), this));

        public LayoutTooltip() {
            highlighted.addListener(this);
            ticker.setCycleCount(Animation.INDEFINITE);
        }

        @Override
        public void handle(ActionEvent event) {
            tooltip.setX(Toolkit.getMouseX() + 10);
            tooltip.setY(Toolkit.getMouseY() + 10);
        }

        @Override
        public void onChanged(Change<? extends Node> c) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < highlighted.size(); i++) {
                if (i != 0) builder.append('\n');
                Node node = highlighted.get(i);
                builder.append("\n"+node.getClass().getName()+": "+String.join( ", ", node.getStyleClass()));
            }
            tooltip.setText(builder.toString());
            Window owner = null;
            if (!highlighted.isEmpty()) {
                Scene scene = highlighted.get(0).getScene();
                if (scene != null) {
                    owner = scene.getWindow();
                }
            }
            if (owner == null) {
                if (tooltip.isShowing()) tooltip.hide();
                ticker.stop();
            } else {
                if (!tooltip.isShowing()) {
                    tooltip.show(owner);
                }
                ticker.play();
            }
        }
    }

    class LayoutDebugger implements EventHandler<Event>, ListChangeListener<Node>, ChangeListener<Background> {

        private static final LayoutTooltip tooltip = new LayoutTooltip();
        private static final Color[] colors = {
                Color.RED,
                Color.YELLOW,
                Color.GREEN,
                Color.ORANGE,
                Color.BLUE,
                Color.AQUA
        };
        private static int index = 0;
        private Background highlighted = new Background(new BackgroundFill(colors[index = (index + 1) % colors.length], null, null));
        private Node node;
        private Background originalBackground;
        public LayoutDebugger(Node node) {
            this.node = node;
        }

        public void register() {
            node.addEventHandler(MouseEvent.MOUSE_ENTERED, this);
            node.addEventHandler(MouseEvent.MOUSE_EXITED, this);
            if (node instanceof Region) {
                originalBackground = ((Region) node).getBackground();
                ((Region) node).backgroundProperty().addListener(this);
            }
            if (node instanceof Parent) {
                ((Parent) node).getChildrenUnmodifiable().addListener(this);
            }
        }

        public void unregister() {
            node.removeEventHandler(MouseEvent.MOUSE_ENTERED, this);
            node.removeEventHandler(MouseEvent.MOUSE_EXITED, this);
            if (node instanceof Region) {
                ((Region) node).backgroundProperty().removeListener(this);
                ((Region) node).setBackground(originalBackground);
            }
            if (node instanceof Parent) {
                ((Parent) node).getChildrenUnmodifiable().removeListener(this);
            }
        }

        @Override
        public void changed(ObservableValue<? extends Background> observable, Background oldValue, Background newValue) {
            if (newValue != highlighted && newValue != originalBackground) {
                originalBackground = newValue;
            }
        }

        @Override
        public void handle(Event event) {
            if (event.getEventType() == MouseEvent.MOUSE_ENTERED) {
                if (node instanceof Region) {
                    ((Region) node).setBackground(highlighted);
                }
                tooltip.highlighted.add(node);
            } else if (event.getEventType() == MouseEvent.MOUSE_EXITED) {
                if (node instanceof Region) {
                    ((Region) node).setBackground(originalBackground);
                }
                tooltip.highlighted.remove(node);
            }
        }

        @Override
        public void onChanged(Change<? extends Node> c) {
            while (c.next()) {
                for (Node added : c.getAddedSubList()) {
                    highlight(added);
                }
                for (Node removed : c.getRemoved()) {
                    unhighlight(removed);
                }
            }
        }
    }
}
