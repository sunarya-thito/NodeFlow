package thito.nodeflow.library.ui;

import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.css.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import thito.nodeflow.internal.*;
import thito.nodeflow.library.*;

import java.util.*;

public class ModernScrollPane extends Pane implements TickableNode {

    private static CssMetaData<ModernScrollPane, Boolean>
            fillWidthCss = Toolkit.cssMetaData(
                "-fx-fill-width",
                StyleConverter.getBooleanConverter(),
                false,
                scrollPane -> scrollPane.fillWidth
            ),
            fillHeightCss = Toolkit.cssMetaData(
                "-fx-fill-height",
                    StyleConverter.getBooleanConverter(),
                    false,
                    scrollPane -> scrollPane.fillHeight
            ),
            fitWidthCss = Toolkit.cssMetaData(
                    "-fx-fit-width",
                    StyleConverter.getBooleanConverter(),
                    false,
                    scrollPane -> scrollPane.fitWidth
            ),
            fitHeightCss = Toolkit.cssMetaData(
                    "-fx-fit-height",
                    StyleConverter.getBooleanConverter(),
                    false,
                    modernScrollPane -> modernScrollPane.fitHeight
            )
    ;
    private Rectangle clip = new Rectangle();
    private StackPane viewport = new StackPane();
    private Pane verticalScrollBar = new Pane();
    private Pane horizontalScrollBar = new Pane();

    private Pane verticalTrack = new Pane();
    private Pane horizontalTrack = new Pane();

    private Pane shadowTop = new Pane();
    private Pane shadowBottom = new Pane();

    private DoubleProperty offsetX = new SimpleDoubleProperty();
    private DoubleProperty offsetY = new SimpleDoubleProperty();

    private DoubleProperty finalOffsetX = new SimpleDoubleProperty();
    private DoubleProperty finalOffsetY = new SimpleDoubleProperty();

    private StyleableBooleanProperty fillWidth = new SimpleStyleableBooleanProperty(fillWidthCss, true);
    private StyleableBooleanProperty fillHeight = new SimpleStyleableBooleanProperty(fillHeightCss, true);

    private StyleableBooleanProperty fitWidth = new SimpleStyleableBooleanProperty(fitWidthCss, false);
    private StyleableBooleanProperty fitHeight = new SimpleStyleableBooleanProperty(fitHeightCss, false);

    private double panOffsetX, panOffsetY;

    public ModernScrollPane() {
        getChildren().addAll(viewport, shadowBottom, shadowTop, verticalScrollBar, horizontalScrollBar, verticalTrack, horizontalTrack);
        verticalTrack.backgroundProperty().bindBidirectional(horizontalTrack.backgroundProperty());
        verticalTrack.setBackground(new Background(new BackgroundFill(Color.color(1, 1, 1, 0.5), new CornerRadii(10), null)));

        verticalScrollBar.backgroundProperty().bindBidirectional(horizontalScrollBar.backgroundProperty());
        verticalScrollBar.setBackground(new Background(new BackgroundFill(Color.color(1, 1, 1, 0.3), new CornerRadii(10), null)));

        verticalScrollBar.setMinWidth(8);
        horizontalScrollBar.setMinHeight(8);

        viewport.minWidthProperty().bind(Bindings.when(fillWidth).then(widthProperty()).otherwise(-1));
        viewport.minHeightProperty().bind(Bindings.when(fillHeight).then(heightProperty()).otherwise(-1));
        viewport.maxWidthProperty().bind(Bindings.when(fitWidth).then(widthProperty()).otherwise(-1));
        viewport.maxHeightProperty().bind(Bindings.when(fitHeight).then(heightProperty()).otherwise(-1));

        verticalTrack.layoutXProperty().bind(verticalScrollBar.layoutXProperty());
        verticalTrack.prefWidthProperty().bind(verticalScrollBar.widthProperty());
        horizontalTrack.layoutYProperty().bind(horizontalScrollBar.layoutYProperty());
        horizontalTrack.prefHeightProperty().bind(horizontalScrollBar.heightProperty());

        shadowTop.setMinHeight(30);
        shadowBottom.minHeightProperty().bind(shadowTop.minHeightProperty());

        shadowTop.minWidthProperty().bind(widthProperty());
        shadowBottom.minWidthProperty().bind(widthProperty());
        shadowBottom.layoutYProperty().bind(heightProperty().subtract(shadowBottom.heightProperty()));

        shadowTop.setMouseTransparent(true);
        shadowBottom.setMouseTransparent(true);

        shadowTop.setBackground(new Background(new BackgroundFill(
                new LinearGradient(0, 0, 0, 1, true, CycleMethod.REPEAT,
                        new Stop(0, Color.color(0, 0, 0, 0.1)),
                        new Stop(1, Color.TRANSPARENT)),
                null, null
        )));

        shadowBottom.setBackground(new Background(new BackgroundFill(
                new LinearGradient(0, 0, 0, 1, true, CycleMethod.REPEAT,
                        new Stop(1, Color.color(0, 0, 0, 0.1)),
                        new Stop(0, Color.TRANSPARENT)),
                null, null
        )));

        clip.widthProperty().bind(widthProperty());
        clip.heightProperty().bind(heightProperty());
        setClip(clip);

        horizontalScrollBar.setOpacity(0);
        verticalScrollBar.setOpacity(0);
        horizontalTrack.setOpacity(0.5);
        verticalTrack.setOpacity(0.5);

        horizontalScrollBar.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> horizontalFocus = true);
        horizontalScrollBar.addEventHandler(MouseEvent.MOUSE_EXITED, e -> horizontalFocus = false);
        verticalScrollBar.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> verticalFocus = true);
        verticalScrollBar.addEventHandler(MouseEvent.MOUSE_EXITED, e -> verticalFocus = false);
        horizontalTrack.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> horizontalTrackFocus = true);
        horizontalTrack.addEventHandler(MouseEvent.MOUSE_EXITED, e -> horizontalTrackFocus = false);
        verticalTrack.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> verticalTrackFocus = true);
        verticalTrack.addEventHandler(MouseEvent.MOUSE_EXITED, e -> verticalTrackFocus = false);
        addEventHandler(MouseEvent.MOUSE_ENTERED, event -> focus = true);
        addEventHandler(MouseEvent.MOUSE_EXITED, event -> focus = false);
        addEventHandler(ScrollEvent.SCROLL, event -> {
            offsetX.set(offsetX.get() - event.getDeltaX() / getWidth());
            offsetY.set(offsetY.get() - event.getDeltaY() / getHeight());
        });
        addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            if (event.getButton() == MouseButton.MIDDLE) {
                event.consume();
                panOffsetX = event.getX();
                panOffsetY = event.getY();
            }
        });
        addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            event.consume();
            panOffsetX = 0;
            panOffsetY = 0;
        });
        verticalScrollBar.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            event.consume();
            double scaledY = event.getY() / (verticalScrollBar.getHeight() - verticalTrack.getHeight());
            offsetY.set(scaledY);
        });
        horizontalScrollBar.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            event.consume();
            double scaledX = event.getX() / (horizontalScrollBar.getWidth() - horizontalTrack.getWidth());
            offsetX.set(scaledX);
        });
        verticalTrack.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            event.consume();
            dragging = true;
            dragOffsetY = verticalTrack.localToParent(0, event.getY()).getY() - verticalTrack.getLayoutY();
        });
        horizontalTrack.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            event.consume();
            dragging = true;
            dragOffsetX = horizontalTrack.localToParent(event.getX(), 0).getX() - horizontalTrack.getLayoutX();
        });
        verticalTrack.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
            event.consume();
            double y = verticalTrack.localToParent(0, event.getY()).getY() - dragOffsetY;
            double scaledY = y / (verticalScrollBar.getHeight() - verticalTrack.getHeight());
            offsetY.set(scaledY);
        });
        horizontalTrack.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
            event.consume();
            double x = horizontalTrack.localToParent(event.getX(), 0).getX() - dragOffsetX;
            double scaledX = x / (horizontalScrollBar.getWidth() - horizontalTrack.getWidth());
            offsetX.set(scaledX);
        });
        verticalTrack.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            event.consume();
            dragging = false;
        });
        horizontalTrack.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            event.consume();
            dragging = false;
        });
    }

    public ModernScrollPane(Node content) {
        this();
        setContent(content);
    }

    private static final double animationSpeed = 0.2;
    private static final double fadeSpeed = 0.05;
    private static final double scrollSpeed = 0.5;
    private boolean focus;
    private boolean dragging;
    private double dragOffsetX, dragOffsetY;
    private boolean verticalFocus, horizontalFocus, verticalTrackFocus, horizontalTrackFocus;
    private double offset = 0;
    private boolean updating;

    @Override
    protected void layoutChildren() {
        updating = true;
        updateSize();
        updating = false;
        super.layoutChildren();
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
        return new ExtraList<>(super.getCssMetaData(), fitWidthCss, fitHeightCss, fillWidthCss, fillHeightCss);
    }

    @Override
    public void requestLayout() {
        if (updating) return;
        super.requestLayout();
    }

    public void tick() {
        updateAnimation();
    }

    public Node getContent() {
        List<Node> children = viewport.getChildren();
        return children.isEmpty() ? null : children.get(0);
    }

    public void setContent(Node node) {
        List<Node> children = viewport.getChildren();
        if (node == null) {
            children.clear();
            return;
        }
        if (children.isEmpty()) {
            children.add(node);
        } else {
            children.set(0, node);
        }
    }

    private void updateAnimation() {
        tick++;
        verticalTrack.setVisible(offset > 0);
        horizontalTrack.setVisible(offset > 0);
        if (focus || dragging) {
            if (offset < 1) offset += animationSpeed;
        } else {
            if (offset > 0) offset -= animationSpeed;
        }
        if (verticalFocus || verticalTrackFocus) {
            if (verticalScrollBar.getOpacity() < 0.4) verticalScrollBar.setOpacity(verticalScrollBar.getOpacity() + fadeSpeed);
        } else {
            if (verticalScrollBar.getOpacity() > 0) verticalScrollBar.setOpacity(verticalScrollBar.getOpacity() - fadeSpeed);
        }
        if (horizontalFocus || horizontalTrackFocus) {
            if (horizontalScrollBar.getOpacity() < 0.4) horizontalScrollBar.setOpacity(horizontalScrollBar.getOpacity() + fadeSpeed);
        } else {
            if (horizontalScrollBar.getOpacity() > 0) horizontalScrollBar.setOpacity(horizontalScrollBar.getOpacity() - fadeSpeed);
        }

        double width = viewport.getWidth();
        double height = viewport.getHeight();

        double overflowHeight = height - getHeight();
        double overflowWidth = width - getWidth();

        Point2D mouse = screenToLocal(Toolkit.getMouseX(), Toolkit.getMouseY());

        if (panOffsetX > 0) {
            double panDiffX = mouse.getX() - panOffsetX;
            offsetX.set(clamp(offsetX.get() + panDiffX / 1200));
        }

        if (panOffsetY > 0) {
            double panDiffY = mouse.getY() - panOffsetY;
            offsetY.set(clamp(offsetY.get() + panDiffY / 1200));
        }

        double diffX = offsetX.get() - finalOffsetX.get();
        double diffY = offsetY.get() - finalOffsetY.get();

        finalOffsetX.set(finalOffsetX.get() + diffX * scrollSpeed);
        finalOffsetY.set(finalOffsetY.get() + diffY * scrollSpeed);

        if (tick % 10 == 0 && !dragging) {
            if (offsetX.get() > 1) offsetX.set(1);
            else if (offsetX.get() < 0) offsetX.set(0);

            if (offsetY.get() > 1) offsetY.set(1);
            else if (offsetY.get() < 0) offsetY.set(0);
        }

        if (viewport.getLayoutY() <= -5) {
            if (shadowTop.getOpacity() < 1) {
                shadowTop.setOpacity(shadowTop.getOpacity() + fadeSpeed);
            }
        } else {
            if (shadowTop.getOpacity() > 0) {
                shadowTop.setOpacity(shadowTop.getOpacity() - fadeSpeed);
            }
        }

        if (-viewport.getLayoutY() <= viewport.getHeight() - getHeight() - 5) {
            if (shadowBottom.getOpacity() < 1) {
                shadowBottom.setOpacity(shadowBottom.getOpacity() + fadeSpeed);
            }
        } else {
            if (shadowBottom.getOpacity() > 0) {
                shadowBottom.setOpacity(shadowBottom.getOpacity() - fadeSpeed);
            }
        }

        viewport.setLayoutX(overflowWidth * -finalOffsetX.get());
        viewport.setLayoutY(overflowHeight * -finalOffsetY.get());

        if (overflowHeight > 0 && overflowWidth > 0) {
            verticalScrollBar.setLayoutX(getWidth() - verticalScrollBar.getWidth() * offset);
            verticalScrollBar.setPrefHeight(getHeight() - horizontalScrollBar.getHeight());
            horizontalScrollBar.setLayoutY(getHeight() - horizontalScrollBar.getHeight() * offset);
            horizontalScrollBar.setPrefWidth(getWidth() - verticalScrollBar.getWidth());
        } else if (overflowHeight > 0) {
            verticalScrollBar.setLayoutX(getWidth() - verticalScrollBar.getWidth() * offset);
            verticalScrollBar.setPrefHeight(getHeight());
            horizontalScrollBar.setLayoutY(getHeight());
            horizontalScrollBar.setPrefWidth(getWidth() - verticalScrollBar.getWidth());
        } else if (overflowWidth > 0) {
            horizontalScrollBar.setLayoutY(getHeight() - horizontalScrollBar.getHeight() * offset);
            horizontalScrollBar.setPrefWidth(getWidth());
            verticalScrollBar.setLayoutX(getWidth());
            verticalScrollBar.setPrefHeight(getHeight() - horizontalScrollBar.getHeight());
        } else {
            horizontalScrollBar.setLayoutY(getHeight());
            horizontalScrollBar.setPrefWidth(getWidth() - verticalScrollBar.getWidth());
            verticalScrollBar.setLayoutX(getWidth());
            verticalScrollBar.setPrefHeight(getHeight() - horizontalScrollBar.getHeight());
        }
    }

    private int tick = 0;

    private void updateSize() {
        double width = viewport.getWidth();
        double height = viewport.getHeight();

        double flowHeight = clamp(getHeight() / height);
        double flowWidth = clamp(getWidth() / width);

        Node node = getContent();
        if (node instanceof Region) {
            if (fitWidth.get()) {
                ((Region) node).setMaxWidth(viewport.getMaxWidth());
            }
            if (fitHeight.get()) {
                ((Region) node).setMaxHeight(viewport.getMaxHeight());
            }
            if (fillWidth.get()) {
                ((Region) node).setMinWidth(viewport.getMinWidth());
            }
            if (fillHeight.get()) {
                ((Region) node).setMinHeight(viewport.getMinHeight());
            }
        }

        verticalTrack.setLayoutY((verticalScrollBar.getHeight() - verticalTrack.getHeight()) * clamp(offsetY.get()));
        verticalTrack.setPrefHeight(verticalScrollBar.getHeight() * flowHeight);
        horizontalTrack.setLayoutX((horizontalScrollBar.getWidth() - horizontalTrack.getWidth()) * clamp(offsetX.get()));
        horizontalTrack.setPrefWidth(horizontalScrollBar.getWidth() * flowWidth);
    }

    private static double clamp(double x) {
        return Math.max(0, Math.min(1, x));
    }

    public boolean isFillWidth() {
        return fillWidth.get();
    }

    public BooleanProperty fillWidthProperty() {
        return fillWidth;
    }

    public void setFillWidth(boolean fillWidth) {
        this.fillWidth.set(fillWidth);
    }

    public boolean isFillHeight() {
        return fillHeight.get();
    }

    public BooleanProperty fillHeightProperty() {
        return fillHeight;
    }

    public void setFillHeight(boolean fillHeight) {
        this.fillHeight.set(fillHeight);
    }

    public boolean isFitWidth() {
        return fitWidth.get();
    }

    public BooleanProperty fitWidthProperty() {
        return fitWidth;
    }

    public void setFitWidth(boolean fitWidth) {
        this.fitWidth.set(fitWidth);
    }

    public boolean isFitHeight() {
        return fitHeight.get();
    }

    public BooleanProperty fitHeightProperty() {
        return fitHeight;
    }

    public void setFitHeight(boolean fitHeight) {
        this.fitHeight.set(fitHeight);
    }
}
