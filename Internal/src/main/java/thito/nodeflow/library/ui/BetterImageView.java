package thito.nodeflow.library.ui;

import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.css.*;
import javafx.geometry.*;
import javafx.scene.canvas.*;
import javafx.scene.image.*;
import thito.nodeflow.internal.*;
import thito.nodeflow.library.*;

import java.util.*;

public class BetterImageView extends CanvasPane {
    private static final CssMetaData<BetterImageView, FitMode> fitModeCss = Toolkit.cssMetaData(
            "-fx-fit-mode", (StyleConverter<?, FitMode>) StyleConverter.getEnumConverter(FitMode.class),
            FitMode.ORIGINAL, imageView -> imageView.fitMode
    );
    private static final CssMetaData<BetterImageView, Pos> alignmentCss = Toolkit.cssMetaData(
            "-fx-alignment",
            (StyleConverter<?, Pos>) StyleConverter.getEnumConverter(Pos.class),
            Pos.TOP_LEFT,
            imageView -> imageView.alignment
    );
    private ObjectProperty<Image> image = new SimpleObjectProperty<>();
    private StyleableObjectProperty<FitMode> fitMode = new SimpleStyleableObjectProperty<>(fitModeCss, FitMode.ORIGINAL);
    private StyleableObjectProperty<Pos> alignment = new SimpleStyleableObjectProperty<>(alignmentCss, Pos.CENTER);

    public BetterImageView() {
        Toolkit.clip(this);
        image.addListener(this::propertyChange);
        fitMode.addListener(this::propertyChange);
        alignment.addListener(this::propertyChange);
        parentProperty().addListener(this::propertyChange);
    }

    public BetterImageView(thito.nodeflow.api.ui.Image image) {
        this();
        if (image != null) {
            imageProperty().bind(image.impl_propertyPeer());
        }
    }

    public BetterImageView(Image image) {
        this();
        setImage(image);
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
        return new ExtraList<>(super.getCssMetaData(), fitModeCss, alignmentCss);
    }

    private void propertyChange(Observable observable) {
        draw();
    }

    @Override
    protected void layoutChildren() {
        draw();
        super.layoutChildren();
    }

    public ObjectProperty<Image> imageProperty() {
        return image;
    }

    public ObjectProperty<FitMode> fitModeProperty() {
        return fitMode;
    }

    public ObjectProperty<Pos> alignmentProperty() {
        return alignment;
    }

    public void setAlignment(Pos alignment) {
        this.alignment.set(alignment);
    }

    public void setFitMode(FitMode fitMode) {
        this.fitMode.set(fitMode);
    }

    public void setImage(thito.nodeflow.api.ui.Image image) {
        setImage(image.impl_propertyPeer().get());
    }
    public void setImage(Image image) {
        this.image.set(image);
    }

    public Image getImage() {
        return image.get();
    }

    public FitMode getFitMode() {
        return fitMode.get();
    }

    public Pos getAlignment() {
        return alignment.get();
    }

    public void draw() {
        if (image == null) return;
        Image image = this.image.get();
        if (image == null) return;
        layout();
        GraphicsContext context = getCanvas().getGraphicsContext2D();
        try {
            // Unsupported Java??
            context.setImageSmoothing(true);
        } catch (Throwable t) {
        }
        double w = image.getWidth();
        double h = image.getHeight();
        double width = getWidth(), height = getHeight();
        context.clearRect(0, 0, getCanvas().getWidth(), getCanvas().getHeight());
        Pos alignment = this.alignment.get();
        HPos horizontalAlignment = alignment.getHpos();
        VPos verticalAlignment = alignment.getVpos();
        double widthRatio = width / w;
        double heightRatio = height / h;
        double x = 0;
        double y = 0;
        double a = 0;
        double b = 0;
        double ratio;
        switch (fitMode.get()) {
            case SCALE:
                a = width;
                b = height;
                break;
            case SCALE_FIT_MAX:
                ratio = Math.max(widthRatio, heightRatio);
                a = w * ratio;
                b = h * ratio;
                break;
            case SCALE_FIT_MIN:
                ratio = Math.min(widthRatio, heightRatio);
                a = w * ratio;
                b = h * ratio;
                break;
            case ORIGINAL:
                a = w;
                b = h;
                break;
        }
        double diffW = a - width;
        double diffH = b - height;
        switch (horizontalAlignment) {
            case CENTER:
                x = -diffW / 2;
                break;
            case RIGHT:
                x = -diffW;
                break;
        }
        switch (verticalAlignment) {
            case CENTER:
                y = -diffH / 2;
                break;
            case BOTTOM:
                y = -diffH;
                break;
        }
        setPrefWidth(a - x);
        setPrefHeight(b - y);
        context.drawImage(image, x, y, a, b);
    }

    public enum FitMode {
        SCALE, SCALE_FIT_MAX, SCALE_FIT_MIN, ORIGINAL;
    }
}
