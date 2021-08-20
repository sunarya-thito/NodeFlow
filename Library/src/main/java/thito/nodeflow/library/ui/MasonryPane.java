package thito.nodeflow.library.ui;

import javafx.animation.*;
import javafx.application.*;
import javafx.beans.*;
import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.collections.*;
import javafx.css.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.util.*;

import java.util.*;

public class MasonryPane extends Pane {
    private static final CssMetaData<MasonryPane, Duration> fxRepositionSpeed = UIHelper.meta("-fx-reposition-speed", StyleConverter.getDurationConverter(), Duration.millis(1), MasonryPane::repositionSpeedProperty);
    private static final CssMetaData<MasonryPane, Orientation> fxOrientation = UIHelper.meta("-fx-orientation", StyleConverter.getEnumConverter(Orientation.class), Orientation.VERTICAL, MasonryPane::orientationProperty);
    private static final CssMetaData<MasonryPane, Number> fxPreferredSize = UIHelper.meta("-fx-preferred-size", StyleConverter.getSizeConverter(), -1, MasonryPane::preferredOrientationSizeProperty);
    private static final CssMetaData<MasonryPane, Number> fxGap = UIHelper.meta("-fx-gap", StyleConverter.getSizeConverter(), 10, MasonryPane::gapProperty);
    private StyleableObjectProperty<Duration> repositionSpeed = new SimpleStyleableObjectProperty<>(fxRepositionSpeed, Duration.ZERO);
    private ObjectProperty<Interpolator> interpolator = new SimpleObjectProperty<>(Interpolator.EASE_IN);
    private StyleableObjectProperty<Orientation> orientation = new SimpleStyleableObjectProperty<>(fxOrientation, Orientation.VERTICAL);
    private StyleableDoubleProperty preferredOrientationSize = new SimpleStyleableDoubleProperty(fxPreferredSize, -1d);
    private StyleableDoubleProperty gap = new SimpleStyleableDoubleProperty(fxGap, 10d);
    private Timeline animation;

    private InvalidationListener updateListener = obs -> Platform.runLater(this::recalculatePosition);
    private ListChangeListener<Node> childrenListener = c -> {
        while (c.next()) {
            if (c.wasRemoved()) {
                for (Node node : c.getRemoved()) {
                    node.layoutBoundsProperty().removeListener(updateListener);
                }
            }
            if (c.wasAdded()) {
                for (Node node : c.getAddedSubList()) {
                    node.layoutBoundsProperty().addListener(updateListener);
                }
            }
        }
    };

    public MasonryPane() {
        getChildren().addListener(childrenListener);
        layoutBoundsProperty().addListener(updateListener);
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
        List<CssMetaData<? extends Styleable, ?>> cssMetaData = new ArrayList<>(super.getCssMetaData());
        cssMetaData.add(fxPreferredSize);
        cssMetaData.add(fxOrientation);
        cssMetaData.add(fxGap);
        cssMetaData.add(fxRepositionSpeed);
        return cssMetaData;
    }

    public StyleableDoubleProperty gapProperty() {
        return gap;
    }

    public StyleableObjectProperty<Duration> repositionSpeedProperty() {
        return repositionSpeed;
    }

    public ObjectProperty<Interpolator> interpolatorProperty() {
        return interpolator;
    }

    public StyleableObjectProperty<Orientation> orientationProperty() {
        return orientation;
    }

    public StyleableDoubleProperty preferredOrientationSizeProperty() {
        return preferredOrientationSize;
    }

    public double getGap() {
        return gap.get();
    }

    public Duration getRepositionSpeed() {
        return repositionSpeed.get();
    }

    public Interpolator getInterpolator() {
        return interpolator.get();
    }

    public Orientation getOrientation() {
        return orientation.get();
    }

    public double getPreferredOrientationSize() {
        return preferredOrientationSize.get();
    }

    public void setGap(double gap) {
        this.gap.set(gap);
    }

    public void setRepositionSpeed(Duration repositionSpeed) {
        this.repositionSpeed.set(repositionSpeed);
    }

    public void setInterpolator(Interpolator interpolator) {
        this.interpolator.set(interpolator);
    }

    public void setOrientation(Orientation orientation) {
        this.orientation.set(orientation);
    }

    public void setPreferredOrientationSize(double preferredOrientationSize) {
        this.preferredOrientationSize.set(preferredOrientationSize);
    }

    private void recalculatePosition() {
        if (animation != null && animation.getStatus() == Animation.Status.RUNNING) {
            animation.setOnFinished(e -> recalculatePosition());
            return;
        }
        Set<KeyValue> values = new LinkedHashSet<>();
        double gap = this.gap.get();
        Interpolator interpolator = this.interpolator.get();
        if (orientation.get() == Orientation.VERTICAL) {
            double preferredOrientationSize = this.preferredOrientationSize.get();
            if (preferredOrientationSize < 0) {
                for (Node node : getChildren()) {
                    preferredOrientationSize = Math.max(preferredOrientationSize, getPreferredWidth(node));
                }
            }
            Iterator<Node> iterator = getChildren().iterator();
            double paneWidth = getWidth();
            int columns = 0;
            double targetWidth = 0;
            double gapTargetWidth = preferredOrientationSize + gap;
            while (targetWidth + (columns == 0 ? preferredOrientationSize : gapTargetWidth) < paneWidth &&
                    gapTargetWidth < paneWidth && gapTargetWidth > 0) {
                targetWidth += columns == 0 ? preferredOrientationSize : gapTargetWidth;
                columns++;
            }
            double[] heightMap = new double[Math.max(1, columns)];
            double difference = paneWidth - targetWidth;
            preferredOrientationSize += difference / heightMap.length;
            gapTargetWidth = preferredOrientationSize + gap;
            while (iterator.hasNext()) {
                double width = 0;
                for (int i = 0; i < heightMap.length && iterator.hasNext(); i++) {
                    Node node = iterator.next();
                    if (node instanceof Region) {
                        values.add(new KeyValue(((Region) node).prefWidthProperty(), preferredOrientationSize, interpolator));
                    }
                    values.add(new KeyValue(node.layoutXProperty(), width, interpolator));
                    values.add(new KeyValue(node.layoutYProperty(), heightMap[i]));
                    Bounds bounds = node.getLayoutBounds();
                    heightMap[i] += bounds.getHeight() + gap;
                    width += gapTargetWidth;
                }
            }
        } else if (orientation.get() == Orientation.HORIZONTAL) {
            double preferredOrientationSize = this.preferredOrientationSize.get();
            if (preferredOrientationSize < 0) {
                for (Node node : getChildren()) {
                    preferredOrientationSize = Math.max(preferredOrientationSize, getPreferredHeight(node));
                }
            }
            Iterator<Node> iterator = getChildren().iterator();
            double paneHeight = getHeight();
            int rows = 0;
            double targetHeight = 0;
            double gapTargetHeight = preferredOrientationSize + gap;
            while (targetHeight < paneHeight && gapTargetHeight < paneHeight && gapTargetHeight > 0) {
                targetHeight += rows == 0 ? preferredOrientationSize : gapTargetHeight;
                rows++;
            }
            double[] widthMap = new double[Math.max(1, rows-1)];
            preferredOrientationSize = Math.max(paneHeight / widthMap.length, preferredOrientationSize);
            gapTargetHeight = preferredOrientationSize + gap;
            while (iterator.hasNext()) {
                double height = 0;
                for (int i = 0; i < widthMap.length & iterator.hasNext(); i++) {
                    Node node = iterator.next();
                    if (node instanceof Region) {
                        values.add(new KeyValue(((Region) node).prefHeightProperty(), preferredOrientationSize, interpolator));
                    }
                    values.add(new KeyValue(node.layoutYProperty(), height, interpolator));
                    values.add(new KeyValue(node.layoutXProperty(), widthMap[i]));
                    Bounds bounds = node.getLayoutBounds();
                    widthMap[i] += bounds.getWidth() + gap;
                    height += gapTargetHeight;
                }
            }
        }
        if (repositionSpeed.get().toMillis() <= 0) {
            for (KeyValue value : values) {
                ((WritableValue) value.getTarget()).setValue(value.getEndValue());
            }
            return;
        }
        animation = new Timeline(new KeyFrame(repositionSpeed.get(), values.toArray(new KeyValue[0])));
        animation.play();
    }

    static double getPreferredHeight(Node node) {
        double oldPreferredWidth = -1;
        if (node instanceof Region) {
            oldPreferredWidth = ((Region) node).getPrefWidth();
            ((Region) node).setPrefWidth(-1);
        }
        double width = node.getLayoutBounds().getWidth();
        double preferred = node.prefHeight(width);
        double result = Math.max(Math.min(preferred, node.maxHeight(width)), node.minHeight(width));
        if (node instanceof Region) {
            ((Region) node).setPrefWidth(oldPreferredWidth);
        }
        return result;
    }

    static double getPreferredWidth(Node node) {
        double oldPreferredWidth = -1;
        if (node instanceof Region) {
            oldPreferredWidth = ((Region) node).getPrefWidth();
            ((Region) node).setPrefWidth(-1);
        }
        double height = node.getLayoutBounds().getHeight();
        double preferred = node.prefWidth(height);
        double result = Math.max(Math.min(preferred, node.maxWidth(height)), node.minWidth(height));
        if (node instanceof Region) {
            ((Region) node).setPrefWidth(oldPreferredWidth);
        }
        return result;
    }

}
