package thito.nodeflow.library.ui;

import javafx.beans.*;
import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.collections.transformation.*;
import javafx.css.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.shape.*;
import javafx.util.*;
import thito.nodeflow.library.task.*;
import thito.nodeflow.library.util.*;

import java.util.*;

/**
 * Full rewrite of javafx ScrollPane
 * Fixes where javafx ScrollPane can't use Node#lookup
 */
public class ScrollPane extends Pane {
    private static final PseudoClass PANNABLE_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("pannable");
    private static final PseudoClass FIT_TO_WIDTH_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("fitToWidth");
    private static final PseudoClass FIT_TO_HEIGHT_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("fitToHeight");
    private static final PseudoClass VERTICAL = PseudoClass.getPseudoClass("vertical");
    private static final PseudoClass HORIZONTAL = PseudoClass.getPseudoClass("horizontal");
    private static final PseudoClass OVERFLOW_X = PseudoClass.getPseudoClass("overflow_x");
    private static final PseudoClass OVERFLOW_Y = PseudoClass.getPseudoClass("overflow_y");
    private static final PseudoClass OVERFLOW_WIDTH = PseudoClass.getPseudoClass("overflow_width");
    private static final PseudoClass OVERFLOW_HEIGHT = PseudoClass.getPseudoClass("overflow_height");
    private static final CssMetaData<ScrollPane, Boolean> fxFillToWidth = UIHelper.meta("-fx-fill-to-width", StyleConverter.getBooleanConverter(), false, ScrollPane::fillToWidthProperty);
    private static final CssMetaData<ScrollPane, Boolean> fxFillToHeight = UIHelper.meta("-fx-fill-to-height", StyleConverter.getBooleanConverter(), false, ScrollPane::fillToHeightProperty);
    private static final CssMetaData<ScrollPane, Boolean> fxFitToWidth = UIHelper.meta("-fx-fit-to-width", StyleConverter.getBooleanConverter(), false, ScrollPane::fitToWidthProperty);
    private static final CssMetaData<ScrollPane, Boolean> fxFitToHeight = UIHelper.meta("-fx-fit-to-height", StyleConverter.getBooleanConverter(), false, ScrollPane::fitToHeightProperty);
    private static final CssMetaData<ScrollPane, Boolean> fxPannable = UIHelper.meta("-fx-pannable", StyleConverter.getBooleanConverter(), true, ScrollPane::pannableProperty);
    private static final CssMetaData<ScrollPane, Boolean> fxOverlayScrollBars = UIHelper.meta("-fx-overlay-scroll-bars", StyleConverter.getBooleanConverter(), true, ScrollPane::overlayScrollBarsProperty);
    private static final CssMetaData<ScrollPane, javafx.scene.control.ScrollPane.ScrollBarPolicy> fxHBarPolicy =
            UIHelper.meta("-fx-hbar-policy", StyleConverter.getEnumConverter(javafx.scene.control.ScrollPane.ScrollBarPolicy.class), javafx.scene.control.ScrollPane.ScrollBarPolicy.AS_NEEDED, ScrollPane::hbarPolicyProperty);
    private static CssMetaData<ScrollPane, javafx.scene.control.ScrollPane.ScrollBarPolicy> fxVBarPolicy =
            UIHelper.meta("-fx-vbar-policy", StyleConverter.getEnumConverter(javafx.scene.control.ScrollPane.ScrollBarPolicy.class), javafx.scene.control.ScrollPane.ScrollBarPolicy.AS_NEEDED, ScrollPane::vbarPolicyProperty);
    private StyleableBooleanProperty fillToHeight = new SimpleStyleableBooleanProperty(fxFillToHeight, false);
    private StyleableBooleanProperty fillToWidth = new SimpleStyleableBooleanProperty(fxFillToWidth, false);
    private StyleableBooleanProperty fitToHeight = new SimpleStyleableBooleanProperty(fxFitToHeight, false);
    private StyleableBooleanProperty fitToWidth = new SimpleStyleableBooleanProperty(fxFitToWidth, false);
    private StyleableBooleanProperty pannable = new SimpleStyleableBooleanProperty(fxPannable, true);
    private StyleableBooleanProperty overlayScrollBars = new SimpleStyleableBooleanProperty(fxOverlayScrollBars, true);
    private StyleableObjectProperty<javafx.scene.control.ScrollPane.ScrollBarPolicy> hBarPolicy = new SimpleStyleableObjectProperty<>(fxHBarPolicy, javafx.scene.control.ScrollPane.ScrollBarPolicy.AS_NEEDED);
    private StyleableObjectProperty<javafx.scene.control.ScrollPane.ScrollBarPolicy> vBarPolicy = new SimpleStyleableObjectProperty<>(fxVBarPolicy, javafx.scene.control.ScrollPane.ScrollBarPolicy.AS_NEEDED);
    private DoubleProperty vvalue = new SimpleDoubleProperty();
    private DoubleProperty hvalue = new SimpleDoubleProperty();
    private DoubleProperty hmin = new SimpleDoubleProperty();
    private DoubleProperty hmax = new SimpleDoubleProperty(100);
    private DoubleProperty vmin = new SimpleDoubleProperty();
    private DoubleProperty vmax = new SimpleDoubleProperty(100);
    private ObjectProperty<Node> content = new SimpleObjectProperty<>();

    private Pane corner = new Pane();
    private Viewport viewport = new Viewport();
    private ScrollBar horizontalScrollBar = new ScrollBar();
    private ScrollBar verticalScrollBar = new ScrollBar();
    private InvalidationListener updater = obs -> update();

    boolean updatingHorizontal, updatingVertical, dragging;
    double panX, panY;
    ScheduledTask panTask;

    BooleanProperty overflowX = new SimpleBooleanProperty(),
    overflowY = new SimpleBooleanProperty(),
    overflowWidth = new SimpleBooleanProperty(),
    overflowHeight = new SimpleBooleanProperty();

    public ScrollPane() {
        getStyleClass().add("scroll-pane");
        getChildren().addAll(viewport, horizontalScrollBar, verticalScrollBar);
        corner.getStyleClass().add("corner");

        pannable.addListener((obs, old, val) -> pseudoClassStateChanged(PANNABLE_PSEUDOCLASS_STATE, val));
        fitToHeight.addListener((obs, old, val) -> pseudoClassStateChanged(FIT_TO_HEIGHT_PSEUDOCLASS_STATE, val));
        fitToWidth.addListener((obs, old, val) -> pseudoClassStateChanged(FIT_TO_WIDTH_PSEUDOCLASS_STATE, val));
        overflowX.addListener((obs, old, val) -> pseudoClassStateChanged(OVERFLOW_X, val));
        overflowY.addListener((obs, old, val) -> pseudoClassStateChanged(OVERFLOW_Y, val));
        overflowWidth.addListener((obs, old, val) -> pseudoClassStateChanged(OVERFLOW_WIDTH, val));
        overflowHeight.addListener((obs, old, val) -> pseudoClassStateChanged(OVERFLOW_HEIGHT, val));

        overflowX.bind(viewport.wrapper.widthProperty().greaterThan(viewport.widthProperty())
        .and(viewport.wrapper.layoutXProperty().lessThan(0)));
        overflowY.bind(viewport.wrapper.heightProperty().greaterThan(viewport.heightProperty())
        .and(viewport.wrapper.layoutYProperty().lessThan(0)));
        overflowWidth.bind(viewport.wrapper.widthProperty().greaterThan(viewport.widthProperty())
        .and(viewport.wrapper.layoutXProperty().greaterThanOrEqualTo(viewport.widthProperty().negate())));
        overflowHeight.bind(viewport.wrapper.heightProperty().greaterThan(viewport.heightProperty())
        .and(viewport.wrapper.layoutYProperty().greaterThanOrEqualTo(viewport.heightProperty().negate())));

        addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            if (pannable.get() && event.getButton() == MouseButton.MIDDLE && !dragging) {
                event.consume();
                dragging = true;
                panX = event.getX();
                panY = event.getY();
                if (panTask != null) panTask.cancel();
                setCursor(Cursor.MOVE);
                panTask = TaskThread.UI().schedule(() -> {
                    if (!dragging) panTask.cancel();
                    Point2D mouse = screenToLocal(Toolkit.mouse());
                    double xDist = (mouse.getX() - panX) / 10;
                    double yDist = (mouse.getY() - panY) / 10;
                    verticalScrollBar.setValue(Math.max(verticalScrollBar.getMin(), Math.min(verticalScrollBar.getMax(), verticalScrollBar.getValue() + yDist)));
                    horizontalScrollBar.setValue(Math.max(horizontalScrollBar.getMin(), Math.min(horizontalScrollBar.getMax(), horizontalScrollBar.getValue() + xDist)));
                }, Duration.ZERO, Duration.millis(16));
            }
        });

        addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            if (dragging && event.getButton() == MouseButton.MIDDLE) {
                setCursor(Cursor.DEFAULT);
                event.consume();
                if (panTask != null) {
                    panTask.cancel();
                }
                dragging = false;
            }
        });

        addEventHandler(ScrollEvent.SCROLL, event -> {
            if (pannable.get()) {
                event.consume();
                verticalScrollBar.setValue(Math.max(verticalScrollBar.getMin(), Math.min(verticalScrollBar.getMax(), verticalScrollBar.getValue() - event.getDeltaY())));
            }
        });

        verticalScrollBar.addEventHandler(ScrollEvent.SCROLL, event -> {
            if (pannable.get()) {
                event.consume();
                verticalScrollBar.setValue(Math.max(verticalScrollBar.getMin(), Math.min(verticalScrollBar.getMax(), verticalScrollBar.getValue() - event.getDeltaY())));
            }
        });

        horizontalScrollBar.addEventHandler(ScrollEvent.SCROLL, event -> {
            if (pannable.get()) {
                event.consume();
                horizontalScrollBar.setValue(Math.max(horizontalScrollBar.getMin(), Math.min(horizontalScrollBar.getMax(), horizontalScrollBar.getValue() - event.getDeltaY())));
            }
        });

        horizontalScrollBar.visibleAmountProperty().bind(viewport.widthProperty().multiply(horizontalScrollBar.maxProperty().divide(viewport.wrapper.widthProperty())));
        verticalScrollBar.visibleAmountProperty().bind(viewport.heightProperty().multiply(verticalScrollBar.maxProperty().divide(viewport.wrapper.heightProperty())));
        horizontalScrollBar.pseudoClassStateChanged(HORIZONTAL, true);
        verticalScrollBar.pseudoClassStateChanged(VERTICAL, true);
        horizontalScrollBar.getStyleClass().add("horizontal-scroll-bar");
        verticalScrollBar.getStyleClass().add("vertical-scroll-bar");
        horizontalScrollBar.setOrientation(Orientation.HORIZONTAL);
        verticalScrollBar.setOrientation(Orientation.VERTICAL);
        horizontalScrollBar.valueProperty().addListener((obs, old, val) -> {
            if (updatingHorizontal) return;
            updatingHorizontal = true;
            hvalue.set((val.doubleValue() / horizontalScrollBar.getMax()) * (hmax.get() - hmin.get()) + hmin.get());
            updatingHorizontal = false;
        });
        hvalue.addListener((obs, old, val) -> {
            if (updatingHorizontal) return;
            updatingHorizontal = true;
            horizontalScrollBar.setValue(((val.doubleValue() - hmin.get()) / (hmax.get() - hmin.get())) * horizontalScrollBar.getMax());
            updatingHorizontal = false;
        });
        horizontalScrollBar.maxProperty().bind(Bindings.max(0, viewport.wrapper.widthProperty().subtract(viewport.widthProperty())));
        verticalScrollBar.valueProperty().addListener((obs, old, val) -> {
            if (updatingVertical) return;
            updatingVertical = true;
            vvalue.set((val.doubleValue() / verticalScrollBar.getMax()) * (vmax.get() - vmin.get()) + vmin.get());
            updatingVertical = false;
        });
        viewport.wrapper.layoutYProperty().bind(
                Bindings.when(viewport.heightProperty().lessThan(viewport.wrapper.heightProperty()))
                .then(vvalue.subtract(vmin).divide(vmax.subtract(vmin)).multiply(verticalScrollBar.maxProperty()).negate())
                .otherwise(0)
        );
        viewport.wrapper.layoutXProperty().bind(
                Bindings.when(viewport.widthProperty().lessThan(viewport.wrapper.widthProperty()))
                .then(hvalue.subtract(hmin).divide(hmax.subtract(hmin)).multiply(horizontalScrollBar.maxProperty()).negate())
                .otherwise(0)
        );
        vvalue.addListener((obs, old, val) -> {
            if (updatingVertical) return;
            updatingVertical = true;
            verticalScrollBar.setValue(((val.doubleValue() - vmin.get()) / (vmax.get() - vmin.get())) * verticalScrollBar.getMax());
            updatingVertical = false;
        });
        verticalScrollBar.maxProperty().bind(Bindings.max(0, viewport.wrapper.heightProperty().subtract(viewport.heightProperty())));

        content.addListener((obs, old, val) -> {
            if (old != null) {
                old.layoutBoundsProperty().removeListener(updater);
            }
            if (val != null) {
                val.layoutBoundsProperty().addListener(updater);
            }
            update();
        });

        verticalScrollBar.visibleProperty().addListener((obs, old, val) -> {
            pseudoClassStateChanged(VERTICAL, val);
        });

        horizontalScrollBar.visibleProperty().addListener((obs, old, val) -> {
            pseudoClassStateChanged(HORIZONTAL, val);
        });

        hBarPolicy.addListener(updater);
        vBarPolicy.addListener(updater);

        layoutBoundsProperty().addListener(updater);

        absoluteWidth(viewport, Bindings.when(overlayScrollBars.or(verticalScrollBar.visibleProperty().not()))
        .then(widthProperty())
        .otherwise(widthProperty().subtract(verticalScrollBar.widthProperty())));
        absoluteHeight(viewport, Bindings.when(overlayScrollBars.or(horizontalScrollBar.visibleProperty().not()))
        .then(heightProperty())
        .otherwise(heightProperty().subtract(horizontalScrollBar.heightProperty())));

        horizontalScrollBar.layoutYProperty().bind(heightProperty().subtract(horizontalScrollBar.heightProperty()));
        absoluteWidth(horizontalScrollBar, Bindings.when(verticalScrollBar.visibleProperty())
        .then(widthProperty().subtract(verticalScrollBar.widthProperty()))
        .otherwise(widthProperty()));

        verticalScrollBar.layoutXProperty().bind(widthProperty().subtract(verticalScrollBar.widthProperty()));
        absoluteHeight(verticalScrollBar, Bindings.when(horizontalScrollBar.visibleProperty())
        .then(heightProperty().subtract(horizontalScrollBar.heightProperty()))
        .otherwise(heightProperty()));

        corner.visibleProperty().bind(verticalScrollBar.visibleProperty().and(horizontalScrollBar.visibleProperty()).and(overlayScrollBars.not()));

        absoluteWidth(corner, verticalScrollBar.widthProperty());
        absoluteHeight(corner, horizontalScrollBar.heightProperty());

        setFitToWidth(true);
        setFillToWidth(true);
        setFillToHeight(true);
    }

    static void absoluteWidth(Region region, ObservableValue<? extends Number> value) {
        region.minWidthProperty().bind(value);
        region.prefWidthProperty().bind(value);
        region.maxWidthProperty().bind(value);
    }

    static void absoluteHeight(Region region, ObservableValue<? extends Number> value) {
        region.minHeightProperty().bind(value);
        region.prefHeightProperty().bind(value);
        region.maxHeightProperty().bind(value);
    }

    private void update() {
        boolean hbar = checkVisibility(hBarPolicy, Orientation.HORIZONTAL);
        boolean vbar = checkVisibility(vBarPolicy, Orientation.VERTICAL);
        horizontalScrollBar.setVisible(hbar);
        verticalScrollBar.setVisible(vbar);
    }

    private boolean checkVisibility(ObjectProperty<javafx.scene.control.ScrollPane.ScrollBarPolicy> policy, Orientation orientation) {
        javafx.scene.control.ScrollPane.ScrollBarPolicy scrollBarPolicy = policy.get();
        if (scrollBarPolicy == javafx.scene.control.ScrollPane.ScrollBarPolicy.ALWAYS) return true;
        if (scrollBarPolicy == javafx.scene.control.ScrollPane.ScrollBarPolicy.NEVER) return false;
        Node content = this.content.get();
        if (content == null) return false;
        Bounds bounds = content.getLayoutBounds();
        if (bounds == null) return false;
        if (orientation == Orientation.VERTICAL) {
            return bounds.getHeight() > getHeight();
        } else {
            return bounds.getWidth() > getWidth();
        }
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
        List<CssMetaData<? extends Styleable, ?>> list = new ArrayList<>(super.getCssMetaData());
        list.add(fxPannable);
        list.add(fxFillToHeight);
        list.add(fxFillToWidth);
        list.add(fxHBarPolicy);
        list.add(fxVBarPolicy);
        list.add(fxOverlayScrollBars);
        list.add(fxFitToWidth);
        list.add(fxFitToHeight);
        return Collections.unmodifiableList(list);
    }

    public ScrollPane(Node content) {
        this();
        setContent(content);
    }

    public boolean isFitToHeight() {
        return fitToHeight.get();
    }

    public StyleableBooleanProperty fitToHeightProperty() {
        return fitToHeight;
    }

    public void setFitToHeight(boolean fitToHeight) {
        this.fitToHeight.set(fitToHeight);
    }

    public boolean isFitToWidth() {
        return fitToWidth.get();
    }

    public StyleableBooleanProperty fitToWidthProperty() {
        return fitToWidth;
    }

    public void setFitToWidth(boolean fitToWidth) {
        this.fitToWidth.set(fitToWidth);
    }

    public boolean isOverlayScrollBars() {
        return overlayScrollBars.get();
    }

    public StyleableBooleanProperty overlayScrollBarsProperty() {
        return overlayScrollBars;
    }

    public void setOverlayScrollBars(boolean overlayScrollBars) {
        this.overlayScrollBars.set(overlayScrollBars);
    }

    public Node getContent() {
        return content.get();
    }

    public javafx.scene.control.ScrollPane.ScrollBarPolicy getHbarPolicy() {
        return hBarPolicy.get();
    }

    public StyleableObjectProperty<javafx.scene.control.ScrollPane.ScrollBarPolicy> hbarPolicyProperty() {
        return hBarPolicy;
    }

    public void setHbarPolicy(javafx.scene.control.ScrollPane.ScrollBarPolicy hBarPolicy) {
        this.hBarPolicy.set(hBarPolicy);
    }

    public javafx.scene.control.ScrollPane.ScrollBarPolicy getVbarPolicy() {
        return vBarPolicy.get();
    }

    public StyleableObjectProperty<javafx.scene.control.ScrollPane.ScrollBarPolicy> vbarPolicyProperty() {
        return vBarPolicy;
    }

    public void setVbarPolicy(javafx.scene.control.ScrollPane.ScrollBarPolicy vBarPolicy) {
        this.vBarPolicy.set(vBarPolicy);
    }

    public ObjectProperty<Node> contentProperty() {
        return content;
    }

    public void setContent(Node content) {
        this.content.set(content);
    }

    public boolean getFillToHeight() {
        return fillToHeight.get();
    }

    public void setFillToHeight(boolean fillToHeight) {
        this.fillToHeight.set(fillToHeight);
    }

    public boolean getFillToWidth() {
        return fillToWidth.get();
    }

    public void setFillToWidth(boolean fillToWidth) {
        this.fillToWidth.set(fillToWidth);
    }

    public boolean isPannable() {
        return pannable.get();
    }

    public void setPannable(boolean pannable) {
        this.pannable.set(pannable);
    }

    public StyleableBooleanProperty pannableProperty() {
        return pannable;
    }

    public StyleableBooleanProperty fillToHeightProperty() {
        return fillToHeight;
    }

    public StyleableBooleanProperty fillToWidthProperty() {
        return fillToWidth;
    }

    @Override
    protected double computePrefHeight(double v) {
        return viewport.wrapper.prefHeight(-1);
    }

    @Override
    protected double computePrefWidth(double v) {
        return viewport.wrapper.prefWidth(-1);
    }

    class Viewport extends Pane {
        private BorderPane wrapper;
        private Rectangle clip;

        public Viewport() {
            wrapper = new BorderPane();
            wrapper.getStyleClass().add("viewport");
            clip = new Rectangle();
            clip.widthProperty().bind(widthProperty());
            clip.heightProperty().bind(heightProperty());
            setClip(clip);
            getChildren().add(wrapper);

            wrapper.centerProperty().bind(content);

            fitToHeight.addListener((obs, old, val) -> {
                wrapper.maxHeightProperty().unbind();
                if (val) {
                    wrapper.maxHeightProperty().bind(heightProperty());
                }
            });
            fitToWidth.addListener((obs, old, val) -> {
                wrapper.maxWidthProperty().unbind();
                if (val) {
                    wrapper.maxWidthProperty().bind(widthProperty());
                }
            });
            fillToHeight.addListener((obs, old, val) -> {
                wrapper.minHeightProperty().unbind();
                if (val) {
                    wrapper.minHeightProperty().bind(heightProperty());
                }
            });
            fillToWidth.addListener((obs, old, val) -> {
                wrapper.minWidthProperty().unbind();
                if (val) {
                    wrapper.minWidthProperty().bind(widthProperty());
                }
            });

            if (fitToHeight.get()) {
                wrapper.maxHeightProperty().bind(heightProperty());
            }

            if (fitToWidth.get()) {
                wrapper.maxWidthProperty().bind(widthProperty());
            }

            if (fillToHeight.get()) {
                wrapper.minHeightProperty().bind(heightProperty());
            }

            if (fillToWidth.get()) {
                wrapper.minWidthProperty().bind(widthProperty());
            }
        }

    }

}
