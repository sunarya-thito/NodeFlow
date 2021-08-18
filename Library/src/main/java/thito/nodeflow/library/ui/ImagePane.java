package thito.nodeflow.library.ui;

import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.css.*;
import javafx.geometry.*;
import javafx.scene.canvas.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.shape.*;

import java.util.*;

public class ImagePane extends Pane {
    public enum Mode {
        FILL, FIT;
    }
    private static final CssMetaData<ImagePane, String> fxImage = UIHelper.meta("-fx-image", StyleConverter.getUrlConverter(), null, ImagePane::imageUrlProperty);
    private static final CssMetaData<ImagePane, Mode> fxHeightMode = UIHelper.meta("-fx-height-mode", StyleConverter.getEnumConverter(Mode.class), Mode.FILL, ImagePane::heightModeProperty);
    private static final CssMetaData<ImagePane, Mode> fxWidthMode = UIHelper.meta("-fx-width-mode", StyleConverter.getEnumConverter(Mode.class), Mode.FILL, ImagePane::widthModeProperty);
    private static final CssMetaData<ImagePane, Pos> fxAlignment = UIHelper.meta("-fx-alignment", StyleConverter.getEnumConverter(Pos.class), Pos.CENTER, ImagePane::alignmentProperty);
    private StyleableObjectProperty<Pos> alignment = new SimpleStyleableObjectProperty<>(fxAlignment, Pos.CENTER);
    private StyleableObjectProperty<Mode> heightMode = new SimpleStyleableObjectProperty<>(fxHeightMode, Mode.FILL);
    private StyleableObjectProperty<Mode> widthMode = new SimpleStyleableObjectProperty<>(fxWidthMode, Mode.FILL);
    private StyleableObjectProperty<String> imageUrl = new SimpleStyleableObjectProperty<>(fxImage) {
        @Override
        protected void invalidated() {
            if (!imageProperty().isBound()) {
                setImage(new Image(get()));
            }
        }
    };

    private ObjectProperty<Image> image = new SimpleObjectProperty<>();
    private ImageView imageView = new ImageView();

    public ImagePane() {
        widthModeProperty().addListener(this::update);
        heightModeProperty().addListener(this::update);
        alignmentProperty().addListener(this::update);
        getChildren().add(imageView);
    }

    void update(Observable o) {
        Image image = imageProperty().get();
        if (image == null) return;
        Mode widthMode = widthModeProperty().get();
        Mode heightMode = heightModeProperty().get();
        double width;
        double height;
        if (widthMode == Mode.FIT) {
            width = getWidth();
        } else {
            if (heightMode == Mode.FIT) {
                width = image.getWidth() * (getHeight() / image.getHeight());
            } else {
                width = image.getWidth();
            }
        }
        if (heightMode == Mode.FIT) {
            height = getHeight();
        } else {
            if (widthMode == Mode.FIT) {
                height = image.getHeight() * (getWidth() / image.getWidth());
            } else {
                height = image.getHeight();
            }
        }
        double x = (getWidth() - width) / 2;
        double y = (getHeight() - height) / 2;
        imageView.setLayoutX(x);
        imageView.setLayoutY(y);
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
    }

    @Override
    protected void layoutChildren() {
        update(null);
        super.layoutChildren();
    }

    public StyleableObjectProperty<Pos> alignmentProperty() {
        return alignment;
    }

    public StyleableObjectProperty<Mode> widthModeProperty() {
        return widthMode;
    }

    public StyleableObjectProperty<Mode> heightModeProperty() {
        return heightMode;
    }

    public StyleableObjectProperty<String> imageUrlProperty() {
        return imageUrl;
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
        List<CssMetaData<? extends Styleable, ?>> cssMetaData = new ArrayList<>(super.getCssMetaData());
        cssMetaData.add(fxImage);
        cssMetaData.add(fxHeightMode);
        cssMetaData.add(fxWidthMode);
        cssMetaData.add(fxAlignment);
        return cssMetaData;
    }

    public ObjectProperty<Image> imageProperty() {
        return imageView.imageProperty();
    }

    public void setImage(Image image) {
        imageProperty().set(image);
    }
}
