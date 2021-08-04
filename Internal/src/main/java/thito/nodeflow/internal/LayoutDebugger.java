package thito.nodeflow.internal;

import javafx.beans.*;
import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.collections.*;
import javafx.css.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.stage.*;
import thito.nodeflow.library.binding.*;
import thito.nodeflow.library.ui.*;

import java.lang.ref.*;
import java.util.*;

public class LayoutDebugger extends Pane implements Tickable {

    Line vMax = new Line(),
    vMin = new Line(),
    hMax = new Line(),
    hMin = new Line();
    Rectangle rectangle = new Rectangle();
    Node node;
    Stage stage;
    Scene scene;
    VBox info = new VBox();
    Node highlightTarget;
    boolean hold;

    public LayoutDebugger(Stage hook) {
        setMouseTransparent(true);
        rectangle.setFill(Color.color(1, 1, 0, 0.5));
        vMax.strokeProperty().bind(vMin.strokeProperty());
        vMin.strokeProperty().bind(hMax.strokeProperty());
        hMax.strokeProperty().bind(hMin.strokeProperty());
        hMin.setStroke(Color.WHITE);
        Bindings.bindContent(vMax.getStrokeDashArray(), hMin.getStrokeDashArray());
        Bindings.bindContent(vMin.getStrokeDashArray(), hMin.getStrokeDashArray());
        Bindings.bindContent(hMax.getStrokeDashArray(), hMin.getStrokeDashArray());
        hMin.getStrokeDashArray().addAll(10d, 5d);
        getChildren().addAll(rectangle, vMin, vMax, hMin, hMax);
        stage = new Stage(StageStyle.UTILITY);
        hook.xProperty().addListener(x -> stage.setX(hook.getX() + hook.getWidth()));
        hook.yProperty().addListener(x -> stage.setY(hook.getY()));
        hook.widthProperty().addListener(x -> stage.setX(hook.getX() + hook.getWidth()));
        hook.heightProperty().addListener(x -> stage.setHeight(hook.getHeight()));
        stage.setWidth(400);
        stage.setTitle("Press F7 to lock component");
        stage.setResizable(false);
        stage.setAlwaysOnTop(true);
        ModernScrollPane scrollPane = new ModernScrollPane(info);
        scrollPane.setFillHeight(true);
        scrollPane.setFillWidth(true);
        scene = new Scene(scrollPane);
        scene.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.F6) {
                if (isEnabled()) {
                    disable();
                } else {
                    enable();
                }
            } else if (event.getCode() == KeyCode.F7) {
                setHold(!isHold());
            }
        });
        stage.setScene(scene);
        stage.setOnHidden(event -> disable());
    }

    public boolean isEnabled() {
        return Ticker.isRegistered(this);
    }

    public boolean isHold() {
        return hold;
    }

    public void setHold(boolean hold) {
        this.hold = hold;
    }

    public void enable() {
        setVisible(true);
        Ticker.register(this);
        stage.show();
    }

    public void disable() {
        setVisible(false);
        Ticker.unregister(this);
        stage.hide();
    }

    private Node infoNode;

    public void setNode(Node node) {
        this.node = node;
    }

    public Node getNode() {
        return node;
    }

    @Override
    public void tick() {

        double mouseX = Toolkit.getMouseX();
        double mouseY = Toolkit.getMouseY();

        List<Node> passedBy = new ArrayList<>();
        Node lookUpResult = hold ? highlightTarget : lookUp(node, mouseX, mouseY, passedBy);

        if (lookUpResult != null && lookUpResult.getScene() == null) lookUpResult = null;

        if (lookUpResult == null) {
            hold = false;
        }

        highlight(infoNode == null ? lookUpResult : infoNode);

        if (highlightTarget != lookUpResult) {
            highlightTarget = lookUpResult;
            if (lookUpResult != null) {
                info.getChildren().clear();
                setInfo(lookUpResult, passedBy);
            }
        }
    }

    private Label label(Object value) {
        if (value instanceof Collection) {
            value = Arrays.deepToString(((Collection<?>) value).toArray());
        }
        Label label = new Label(String.valueOf(value));
        label.setWrapText(true);
        return label;
    }

    private Label label(ReadOnlyProperty<?> value) {
        Label label = new Label();
        ChangeListener<Object> listener;
        value.addListener(listener = new WeakReferencedChangeListener<Object>(new WeakReference<>(label)) {
            @Override
            public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                label.setText(value.getName()+": "+newValue);
            }
        });
        listener.changed(null, null, value.getValue());
        label.setWrapText(true);
        return label;
    }

    private abstract class WeakSetChangeListener<T> implements SetChangeListener<T>, WeakListener {
        private WeakReference<?> reference;

        @Override
        public boolean wasGarbageCollected() {
            return reference.get() == null;
        }
    }

    private void setInfo(Node node, List<Node> passedBy) {
        this.infoNode = node;
        this.info.getChildren().addAll(
                label(node.getClass().getName()),
                new VBox(
                        label("Style Classes"),
                        label(node.getStyleClass().toString())
                )
        );
        VBox passed = new VBox();
        passed.getChildren().add(label("Passed By"));
        for (Node pass : passedBy) {
            Label label = label(pass.getClass().getName()+": "+pass.getStyleClass());
            HBox boxed = new HBox(label);
            if (pass == node) {
                boxed.setBackground(new Background(new BackgroundFill(Color.YELLOW, null, null)));
            }
            passed.getChildren().add(boxed);
            boxed.setOnMouseClicked(event -> {
                info.getChildren().clear();
                setInfo(pass, passedBy);
            });
        }
        info.getChildren().add(passed);
        info.getChildren().add(label("Properties"));
        VBox props = new VBox();
        for (Map.Entry<Object, Object> object : node.getProperties().entrySet()) {
            props.getChildren().addAll(label(object.getKey()), label(object.getValue()));
        }
        info.getChildren().add(props);
        VBox pseudos = new VBox(new Label("Pseudos"));
        node.getPseudoClassStates().addListener(new WeakSetChangeListener<PseudoClass>() {
            @Override
            public void onChanged(Change<? extends PseudoClass> change) {
                if (change.wasAdded()) {
                    pseudos.getChildren().add(new Label(change.getElementAdded().toString()));
                }
                if (change.wasRemoved()) {
                    pseudos.getChildren().removeIf(x ->
                        x instanceof Label && ((Label) x).getText().equals(change.getElementRemoved().getPseudoClassName())
                    );
                }
            }
        });
        this.info.getChildren().add(pseudos);
        if (node instanceof Region) {
            Region region = (Region) node;
            info.getChildren().addAll(
                    label(region.widthProperty()),
                    label(region.heightProperty())
            );
        }
        pushStyle(node, node.getCssMetaData());
        boolean odd = false;
        for (Node cx : info.getChildren()) {
            if (odd && cx instanceof Region) {
                ((Region) cx).setBackground(new Background(new BackgroundFill(Color.color(0, 0, 0, 0.2), null, null)));
            }
            odd = !odd;
        }
    }

    private void pushStyle(Node node, List<CssMetaData<? extends Styleable, ?>> list) {
        if (list == null) return;
        for (CssMetaData metaData : list) {
            StyleableProperty<?> property = metaData.getStyleableProperty(node);
            if (property == null) {
                Label info = label(metaData.getProperty());
                Label infoValue = label(String.valueOf(metaData.getInitialValue(node)));
                VBox box = new VBox(info, infoValue);
                this.info.getChildren().add(box);
                continue;
            }
            Label info = label(metaData.getProperty());
            Label infoValue = label(String.valueOf(property.getValue()));
            VBox box = new VBox(info, infoValue);
            this.info.getChildren().add(box);
            pushStyle(node, metaData.getSubProperties());
        }
    }

    private void highlight(Node node) {
        vMin.setStartY(0);
        vMin.setEndY(getHeight());
        vMax.setStartY(0);
        vMax.setEndY(getHeight());
        hMin.setStartX(0);
        hMin.setEndX(getWidth());
        hMax.setStartX(0);
        hMax.setEndX(getWidth());
        if (node == null) {
            vMin.setStartX(-1);
            vMin.setEndX(-1);
            vMax.setStartX(-1);
            vMax.setEndX(-1);
            hMin.setStartY(-1);
            hMin.setEndY(-1);
            hMax.setStartY(-1);
            hMax.setEndY(-1);
            rectangle.setX(-1);
            rectangle.setY(-1);
            rectangle.setWidth(0);
            rectangle.setHeight(0);
        } else {
            Bounds screen = node.localToScene(node.getBoundsInLocal());
            screen = sceneToLocal(screen);
            vMin.setStartX(screen.getMinX());
            vMin.setEndX(screen.getMinX());
            vMax.setStartX(screen.getMaxX());
            vMax.setEndX(screen.getMaxX());
            hMin.setStartY(screen.getMinY());
            hMin.setEndY(screen.getMinY());
            hMax.setStartY(screen.getMaxY());
            hMax.setEndY(screen.getMaxY());
            rectangle.setX(screen.getMinX());
            rectangle.setY(screen.getMinY());
            rectangle.setWidth(screen.getWidth());
            rectangle.setHeight(screen.getHeight());
        }
    }

    private Node lookUp(Node node, double x, double y, List<Node> passedBy) {
        if (node != null) {
            Point2D local = node.screenToLocal(x, y);
            Bounds bounds = node.getBoundsInLocal();
            if (bounds != null && bounds.contains(local)) {
                passedBy.add(node);
                if (node instanceof Parent) {
                    Node result = null;
                    for (Node child : ((Parent) node).getChildrenUnmodifiable()) {
                        if (!child.isVisible() || child.getOpacity() <= 0) continue;
                        Node test = lookUp(child, x, y, passedBy);
                        if (test != null) result = test;
                    }
                    if (result != null) {
                        return result;
                    }
                }
                return node;
            }
        }
        return null;
    }

}
