package thito.nodeflow.engine.node.skin;

import javafx.beans.property.*;
import javafx.css.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.shape.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;

public abstract class Skin extends Pane {
    public Skin() {
        reloadCSS();
        getStyleClass().add(getClass().getSimpleName());
    }

    public void reloadCSS() {
        getStylesheets().clear();
        getStylesheets().add(getClass().getName().replace('.', '/')+".css");
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
        List<CssMetaData<? extends Styleable, ?>> list = new ArrayList<>(super.getCssMetaData());
        for (Field field : getClass().getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers()) && field.getType() == CssMetaData.class) {
                field.setAccessible(true);
                try {
                    list.add((CssMetaData<? extends Styleable, ?>) field.get(null));
                } catch (Throwable t) {
                }
            }
        }
        return list;
    }

    public static <T extends Node> T skin(T value, String name) {
        value.getStyleClass().add(name);
        return value;
    }

    public static void clip(Region node, double radius) {
        Rectangle r = new Rectangle();
        r.widthProperty().bind(node.widthProperty());
        r.heightProperty().bind(node.heightProperty());
        r.setArcHeight(radius);
        r.setArcWidth(radius);
        node.setClip(r);
    }

    public static <T, K extends Skin> CssMetaData<K, T> meta(String name, StyleConverter<?, T> converter, T initialValue, Function<K, StyleableProperty<T>> apply) {
        return new CssMetaData<K, T>(name, converter, initialValue, false, new ArrayList<>()) {
            @Override
            public boolean isSettable(K styleable) {
                StyleableProperty<T> property = getStyleableProperty(styleable);
                return property != null && (!(property instanceof Property) || !((Property<?>) property).isBound());
            }

            @Override
            public StyleableProperty<T> getStyleableProperty(K styleable) {
                return apply.apply(styleable);
            }
        };
    }

    public static class DragInfo {
        BooleanProperty dragging = new SimpleBooleanProperty();
        BooleanProperty snapToGrid = new SimpleBooleanProperty();
        double x, y;

        public BooleanProperty draggingProperty() {
            return dragging;
        }

        public BooleanProperty snapToGridProperty() {
            return snapToGrid;
        }
    }
}
