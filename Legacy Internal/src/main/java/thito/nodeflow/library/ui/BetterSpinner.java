package thito.nodeflow.library.ui;

import com.jfoenix.controls.*;
import javafx.css.*;
import javafx.scene.control.*;
import javafx.scene.paint.*;
import thito.nodeflow.internal.*;
import thito.nodeflow.library.*;

import java.util.*;

public class BetterSpinner extends JFXSpinner {
    private static final CssMetaData<BetterSpinner, Paint> strokePaintMetadata =
            Toolkit.paintCssMetaData("-fx-spinner-color", BetterSpinner::strokePaintProperty);
    private StyleableObjectProperty<Paint> strokePaint =
            new SimpleStyleableObjectProperty<>(strokePaintMetadata);

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
        return new ExtraList<>(super.getControlCssMetaData(), strokePaintMetadata);
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new BetterSpinnerSkin(this);
    }

    public Paint getStrokePaint() {
        return strokePaint.get();
    }

    public StyleableObjectProperty<Paint> strokePaintProperty() {
        return strokePaint;
    }

    public void setStrokePaint(Paint strokePaint) {
        this.strokePaint.set(strokePaint);
    }
}
