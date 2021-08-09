package thito.nodeflow.library.ui;

import com.jfoenix.controls.*;
import com.sun.javafx.scene.control.behavior.*;
import com.sun.javafx.scene.control.skin.*;
import javafx.animation.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.util.*;

import java.util.*;

public class BetterSpinnerSkin extends BehaviorSkinBase<JFXSpinner, BehaviorBase<JFXSpinner>> {
    private JFXSpinner control;
    private boolean isValid = false;
    private Timeline timeline;
    private Arc arc;
    private Arc track;
    private final StackPane arcPane;
    private final Rectangle fillRect;
    private double arcLength = -1.0D;
    private Text text;
    boolean wasIndeterminate = false;

    public BetterSpinnerSkin(BetterSpinner control) {
        super(control, new BehaviorBase(control, Collections.emptyList()));
        this.control = control;
        this.arc = new Arc();
        this.arc.setManaged(false);
        this.arc.setStartAngle(0.0D);
        this.arc.setLength(180.0D);
        this.arc.getStyleClass().setAll(new String[]{"arc"});
        this.arc.setFill(javafx.scene.paint.Color.TRANSPARENT);
        this.arc.strokeProperty().bind(control.strokePaintProperty());
        this.arc.setStrokeWidth(3.0D);
        this.track = new Arc();
        this.track.setManaged(false);
        this.track.setStartAngle(0.0D);
        this.track.setLength(360.0D);
        this.track.setStrokeWidth(3.0D);
        this.track.getStyleClass().setAll(new String[]{"track"});
        this.track.setFill(javafx.scene.paint.Color.TRANSPARENT);
        this.fillRect = new Rectangle();
        this.fillRect.setFill(Color.TRANSPARENT);
        this.text = new Text();
        this.text.getStyleClass().setAll(new String[]{"text", "percentage"});
        Group group = new Group(new Node[]{this.fillRect, this.track, this.arc, this.text});
        group.setManaged(false);
        this.arcPane = new StackPane(new Node[]{group});
        this.arcPane.setPrefSize(50.0D, 50.0D);
        this.getChildren().setAll(new Node[]{this.arcPane});
        this.registerChangeListener(control.indeterminateProperty(), "INDETERMINATE");
        this.registerChangeListener(control.progressProperty(), "PROGRESS");
        this.registerChangeListener(control.visibleProperty(), "VISIBLE");
        this.registerChangeListener(control.parentProperty(), "PARENT");
        this.registerChangeListener(control.sceneProperty(), "SCENE");
    }

    protected void handleControlPropertyChanged(String p) {
        super.handleControlPropertyChanged(p);
        if ("INDETERMINATE".equals(p)) {
            this.initialize();
        } else if ("PROGRESS".equals(p)) {
            this.updateProgress();
        } else if ("VISIBLE".equals(p)) {
            this.updateAnimation();
        } else if ("PARENT".equals(p)) {
            this.updateAnimation();
        } else if ("SCENE".equals(p)) {
            this.updateAnimation();
        }

    }

    private void initialize() {
        if (this.getSkinnable().isIndeterminate()) {
            if (this.timeline == null) {
                this.createTransition();
                if (this.getSkinnable().impl_isTreeVisible()) {
                    this.timeline.play();
                }
            }
        } else {
            this.clearAnimation();
            this.arc.setStartAngle(90.0D);
            this.updateProgress();
        }

    }

    private KeyFrame[] getKeyFrames(double angle, double duration) {
        KeyFrame[] frames = new KeyFrame[]{new KeyFrame(Duration.seconds(duration), new KeyValue[]{new KeyValue(this.arc.lengthProperty(), 5, Interpolator.LINEAR), new KeyValue(this.arc.startAngleProperty(), angle + 45.0D + this.control.getStartingAngle(), Interpolator.LINEAR)}), new KeyFrame(Duration.seconds(duration + 0.4D), new KeyValue[]{new KeyValue(this.arc.lengthProperty(), 250, Interpolator.LINEAR), new KeyValue(this.arc.startAngleProperty(), angle + 90.0D + this.control.getStartingAngle(), Interpolator.LINEAR)}), new KeyFrame(Duration.seconds(duration + 0.7D), new KeyValue[]{new KeyValue(this.arc.lengthProperty(), 250, Interpolator.LINEAR), new KeyValue(this.arc.startAngleProperty(), angle + 135.0D + this.control.getStartingAngle(), Interpolator.LINEAR)}), new KeyFrame(Duration.seconds(duration + 1.1D), new KeyValue[]{new KeyValue(this.arc.lengthProperty(), 5, Interpolator.LINEAR), new KeyValue(this.arc.startAngleProperty(), angle + 435.0D + this.control.getStartingAngle(), Interpolator.LINEAR)})};
        return frames;
    }

    private void pauseTimeline(boolean pause) {
        if (this.getSkinnable().isIndeterminate()) {
            if (this.timeline == null) {
                this.createTransition();
            }

            if (pause) {
                this.timeline.pause();
            } else {
                this.timeline.play();
            }
        }

    }

    private void updateAnimation() {
        ProgressIndicator control = this.getSkinnable();
        boolean isTreeVisible = control.isVisible() && control.getParent() != null && control.getScene() != null;
        if (this.timeline != null) {
            this.pauseTimeline(!isTreeVisible);
        } else if (isTreeVisible) {
            this.createTransition();
        }

    }

    protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return -1.0D == this.control.getRadius() ? super.computeMaxHeight(width, topInset, rightInset, bottomInset, leftInset) : this.control.getRadius() * 2.0D + this.arc.getStrokeWidth() * 2.0D;
    }

    protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return -1.0D == this.control.getRadius() ? super.computeMaxHeight(height, topInset, rightInset, bottomInset, leftInset) : this.control.getRadius() * 2.0D + this.arc.getStrokeWidth() * 2.0D;
    }

    protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return this.arcPane.prefWidth(-1.0D);
    }

    protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return this.arcPane.prefHeight(-1.0D);
    }

    protected void layoutChildren(double contentX, double contentY, double contentWidth, double contentHeight) {
        double strokeWidth = this.arc.getStrokeWidth();
        double radius = Math.min(contentWidth, contentHeight) / 2.0D - strokeWidth / 2.0D;
        double arcSize = this.snapSize(radius * 2.0D + strokeWidth);
        this.arcPane.resizeRelocate((contentWidth - arcSize) / 2.0D + 1.0D, (contentHeight - arcSize) / 2.0D + 1.0D, arcSize, arcSize);
        this.updateArcLayout(radius, arcSize);
        this.fillRect.setWidth(arcSize);
        this.fillRect.setHeight(arcSize);
        if (!this.isValid) {
            this.initialize();
            this.isValid = true;
        }

        if (!this.getSkinnable().isIndeterminate()) {
            this.arc.setLength(this.arcLength);
            if (this.text.isVisible()) {
                double progress = this.control.getProgress();
                int intProgress = (int)Math.round(progress * 100.0D);
                Font font = this.text.getFont();
                this.text.setFont(Font.font(font.getFamily(), radius / 1.7D));
                this.text.setText((progress > 1.0D ? 100 : intProgress) + "%");
                this.text.relocate((arcSize - this.text.getLayoutBounds().getWidth()) / 2.0D, (arcSize - this.text.getLayoutBounds().getHeight()) / 2.0D);
            }
        }

    }

    private void updateArcLayout(double radius, double arcSize) {
        this.arc.setRadiusX(radius);
        this.arc.setRadiusY(radius);
        this.arc.setCenterX(arcSize / 2.0D);
        this.arc.setCenterY(arcSize / 2.0D);
        this.track.setRadiusX(radius);
        this.track.setRadiusY(radius);
        this.track.setCenterX(arcSize / 2.0D);
        this.track.setCenterY(arcSize / 2.0D);
        this.track.setStrokeWidth(this.arc.getStrokeWidth());
    }

    protected void updateProgress() {
        ProgressIndicator control = this.getSkinnable();
        boolean isIndeterminate = control.isIndeterminate();
        if (!isIndeterminate || !this.wasIndeterminate) {
            this.arcLength = -360.0D * control.getProgress();
            control.requestLayout();
        }

        this.wasIndeterminate = isIndeterminate;
    }

    private void createTransition() {
        if (this.getSkinnable().isIndeterminate()) {
            KeyFrame[] blueFrame = this.getKeyFrames(0.0D, 0.0D);
            KeyFrame[] redFrame = this.getKeyFrames(450.0D, 1.4D);
            KeyFrame[] yellowFrame = this.getKeyFrames(900.0D, 2.8D);
            KeyFrame[] greenFrame = this.getKeyFrames(1350.0D, 4.2D);
            KeyFrame endingFrame = new KeyFrame(Duration.seconds(5.6D), new KeyValue[]{new KeyValue(this.arc.lengthProperty(), 5, Interpolator.LINEAR), new KeyValue(this.arc.startAngleProperty(), 1845.0D + this.control.getStartingAngle(), Interpolator.LINEAR)});
            if (this.timeline != null) {
                this.timeline.stop();
                this.timeline.getKeyFrames().clear();
            }

            this.timeline = new Timeline(new KeyFrame[]{blueFrame[0], blueFrame[1], blueFrame[2], blueFrame[3], redFrame[0], redFrame[1], redFrame[2], redFrame[3], yellowFrame[0], yellowFrame[1], yellowFrame[2], yellowFrame[3], greenFrame[0], greenFrame[1], greenFrame[2], greenFrame[3], endingFrame});
            this.timeline.setCycleCount(-1);
            this.timeline.setDelay(Duration.ZERO);
            this.timeline.playFromStart();
        }
    }

    private void clearAnimation() {
        if (this.timeline != null) {
            this.timeline.stop();
            this.timeline.getKeyFrames().clear();
            this.timeline = null;
        }

    }

    public void dispose() {
        super.dispose();
        this.clearAnimation();
        this.arc = null;
        this.track = null;
        this.control = null;
    }
}
