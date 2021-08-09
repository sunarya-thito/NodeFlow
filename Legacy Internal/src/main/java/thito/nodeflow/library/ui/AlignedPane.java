package thito.nodeflow.library.ui;

import javafx.beans.property.*;
import javafx.collections.*;
import javafx.css.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import thito.nodeflow.internal.*;
import thito.nodeflow.library.*;

import java.util.*;

public class AlignedPane extends Pane {

    private static final CssMetaData<AlignedPane, Pos> alignmentMetaData = Toolkit.cssMetaData(
            "-fx-alignment",
            (StyleConverter<?, Pos>) StyleConverter.getEnumConverter(Pos.class),
            Pos.TOP_LEFT,
            alignedPane -> alignedPane.alignmentProperty()
    );
    private StyleableObjectProperty<Pos> alignment = new SimpleStyleableObjectProperty<>(alignmentMetaData, Pos.TOP_LEFT);
    public AlignedPane() {
        getChildren().addListener((ListChangeListener<Node>) c -> {
            while (c.next()) {
                if (c.wasRemoved()) {
                    for (Node remove : c.getRemoved()) {
                        remove.layoutXProperty().unbind();
                        remove.layoutYProperty().unbind();
                    }
                }
                if (c.wasAdded()) {
                    for (Node added : c.getAddedSubList()) {
                        setupPosition(added);
                    }
                }
            }
        });
        alignmentProperty().addListener(e -> {
            for (Node child : getChildren()) {
                setupPosition(child);
            }
        });
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
        return new ExtraList<>(super.getCssMetaData(), alignmentMetaData);
    }

    protected void setupPosition(Node node) {
        switch (getAlignment()) {
            case TOP_LEFT:
            case CENTER_LEFT:
            case BASELINE_LEFT:
            case BOTTOM_LEFT:
                node.layoutXProperty().unbind();
                node.layoutXProperty().set(0);
                break;
            case TOP_CENTER:
            case CENTER:
            case BASELINE_CENTER:
            case BOTTOM_CENTER:
                node.layoutXProperty().bind(widthProperty().subtract(widthProperty(node)).divide(2));
                break;
            case TOP_RIGHT:
            case CENTER_RIGHT:
            case BASELINE_RIGHT:
            case BOTTOM_RIGHT:
                node.layoutXProperty().bind(widthProperty().subtract(widthProperty(node)));
                break;
        }
        switch (getAlignment()) {
            case TOP_LEFT:
            case TOP_CENTER:
            case TOP_RIGHT:
                node.layoutYProperty().unbind();
                node.layoutYProperty().set(0);
                break;
            case CENTER:
            case CENTER_LEFT:
            case CENTER_RIGHT:
            case BASELINE_CENTER:
                node.layoutYProperty().bind(heightProperty().subtract(heightProperty(node)).divide(2));
                break;
            case BOTTOM_CENTER:
            case BOTTOM_LEFT:
            case BOTTOM_RIGHT:
                node.layoutYProperty().bind(heightProperty().subtract(heightProperty(node)));
                break;
        }
    }

    protected ReadOnlyDoubleProperty heightProperty(Node node) {
        if (node instanceof Region) {
            return ((Region) node).heightProperty();
        }
        return new SimpleDoubleProperty();
    }

    protected ReadOnlyDoubleProperty widthProperty(Node node) {
        if (node instanceof Region) {
            return ((Region) node).widthProperty();
        }
        return new SimpleDoubleProperty();
    }

    public Pos getAlignment() {
        return alignment.get();
    }

    public StyleableObjectProperty<Pos> alignmentProperty() {
        return alignment;
    }

    public void setAlignment(Pos alignment) {
        this.alignment.set(alignment);
    }
}
