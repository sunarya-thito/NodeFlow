package thito.nodeflow.library.ui.injection;

import com.jfoenix.controls.*;
import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.css.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.shape.*;
import thito.nodeflow.internal.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;

public class StyleInjector {

    public static final CssMetaData<ImageView, Number> FIT_WIDTH =
            create("-fx-fit-width", ImageView.class, StyleConverter.getSizeConverter(), ImageView::fitWidthProperty);
    public static final CssMetaData<ImageView, Number> FIT_HEIGHT =
            create("-fx-fit-height", ImageView.class, StyleConverter.getSizeConverter(), ImageView::fitHeightProperty);
    public static final CssMetaData<Region, Number> ABSOLUTE_HEIGHT =
            createDirect("-fx-absolute-height", Region.class, StyleConverter.getSizeConverter(), (node, css) -> {
                SimpleStyleableDoubleProperty property = new SimpleStyleableDoubleProperty(css) {
                    @Override
                    protected void invalidated() {
                        node.setMaxHeight(get());
                        node.setMinHeight(get());
                        node.setPrefHeight(get());
                    }
                };
                return property;
            });
    public static final CssMetaData<ScrollPane, Boolean> DISABLE_SCROLL_PANE_BEHAVIOUR = createDirect("-fx-disable-scroll-pane-behaviour", ScrollPane.class, StyleConverter.getBooleanConverter(), (node, css) -> {
        return new SimpleStyleableBooleanProperty(css) {

            {
                node.contentProperty().addListener(this::changed);
            }

            protected void changed(Observable obs) {
                invalidated();
            }

            @Override
            protected void invalidated() {
                if (node.getContent() != null) {
                    if (get()) {
                        node.minHeightProperty().bind(((Region) node.getContent()).heightProperty());
                        node.minWidthProperty().bind(((Region) node.getContent()).widthProperty());
                    } else {
                        node.minHeightProperty().unbind();
                        node.minWidthProperty().unbind();
                    }
                }
            }
        };
    });
    public static final CssMetaData<Region, Number> ABSOLUTE_WIDTH =
            createDirect("-fx-absolute-width", Region.class, StyleConverter.getSizeConverter(), (node, css) -> {
                SimpleStyleableDoubleProperty property = new SimpleStyleableDoubleProperty(css) {
                    @Override
                    protected void invalidated() {
                        node.setMaxWidth(get());
                        node.setMinWidth(get());
                        node.setPrefWidth(get());
                    }
                };
                return property;
            });

    public static final CssMetaData<Circle, Number> RADIUS = create("-fx-radius", Circle.class, StyleConverter.getSizeConverter(), Circle::radiusProperty);

    public static final CssMetaData<Node, Boolean> MANAGED = create("-fx-managed", Node.class, StyleConverter.getBooleanConverter(), Node::managedProperty);

    public static final CssMetaData<Node, Boolean> VISIBLE = create("-fx-visible", Node.class, StyleConverter.getBooleanConverter(), Node::visibleProperty);

    public static final CssMetaData<ImageView, Boolean> PRESERVE_RATIO = create("-fx-preserve-ratio", ImageView.class, StyleConverter.getBooleanConverter(), ImageView::preserveRatioProperty);

    public static final CssMetaData<JFXRippler, JFXRippler.RipplerPos> RIPPLER_POS = create("-jfx-rippler-pos", JFXRippler.class, (StyleConverter<String, JFXRippler.RipplerPos>) StyleConverter.getEnumConverter(JFXRippler.RipplerPos.class), JFXRippler::positionProperty);

    public static void initialize() {
        inject(ImageView.class, FIT_HEIGHT);
        inject(ImageView.class, FIT_WIDTH);
        inject(Region.class, ABSOLUTE_WIDTH);
        inject(Region.class, ABSOLUTE_HEIGHT);
        inject(Circle.class, RADIUS);
        inject(Node.class, MANAGED);
        inject(Node.class, VISIBLE);
        inject(JFXRippler.class, RIPPLER_POS);
        inject(ImageView.class, PRESERVE_RATIO);
        inject(ScrollPane.class, DISABLE_SCROLL_PANE_BEHAVIOUR);
    }

    public static <T extends Node, K> CssMetaData<T, K> create(String name, StyleConverter<?, K> converter) {
        return new CssMetaData<T, K>(name, converter) {

            @Override
            public boolean isSettable(T styleable) {
                StyleableObjectProperty<K> property = getStyleableProperty(styleable);
                return !property.isBound();
            }

            @Override
            public StyleableObjectProperty<K> getStyleableProperty(T styleable) {
                return (StyleableObjectProperty<K>) styleable.getProperties().computeIfAbsent(this, css -> new SimpleStyleableObjectProperty<>(this));
            }

        };
    }

    public static <T extends Node, K> CssMetaData<T, K> createDirect(String name, Class<T> clazz, StyleConverter<?, K> converter, Function<T, StyleableProperty<K>> getter) {
        return new CssMetaData<T, K>(name, converter) {
            @Override
            public boolean isSettable(T styleable) {
                StyleableProperty<K> property = getStyleableProperty(styleable);
                return property != null && (!(property instanceof Property) || !((Property<?>) property).isBound());
            }

            @Override
            public StyleableProperty<K> getStyleableProperty(T styleable) {
                if (!clazz.isInstance(styleable)) return null;
                return getter.apply(styleable);
            }
        };
    }

    public static <T extends Node, K> CssMetaData<T, K> createDirect(String name, Class<T> clazz, StyleConverter<?, K> converter, BiFunction<T, CssMetaData<T, K>, StyleableProperty<K>> getter) {
        return new CssMetaData<T, K>(name, converter) {
            @Override
            public boolean isSettable(T styleable) {
                StyleableProperty<K> property = getStyleableProperty(styleable);
                return property != null && (!(property instanceof Property) || !((Property<?>) property).isBound());
            }

            @Override
            public StyleableProperty<K> getStyleableProperty(T styleable) {
                if (!clazz.isInstance(styleable)) return null;
                return getter.apply(styleable, this);
            }
        };
    }

    public static <T extends Node, K> CssMetaData<T, K> create(String name, Class<?> clazz, StyleConverter<?, K> converter, Function<T, Property<K>> getter) {
        return new CssMetaData<T, K>(name, converter) {
            @Override
            public boolean isSettable(T styleable) {
                if (!clazz.isInstance(styleable)) return false;
                Property<K> property = getter.apply(styleable);
                return property != null && !property.isBound();
            }

            @Override
            public StyleableProperty<K> getStyleableProperty(T styleable) {
                if (!clazz.isInstance(styleable)) return null;
                StyleableProperty<K> property = (StyleableProperty<K>) styleable.getProperties().computeIfAbsent(this, css -> {
                    StyleableObjectProperty<K> prop = new SimpleStyleableObjectProperty<>(this);
                    Property<K> bind = getter.apply(styleable);
                    prop.bindBidirectional(bind);
                    return prop;
                });
                return property;
            }
        };
    }

    public static Class<?> findInnerClass(Class<?> clazz) {
        if (clazz == null) return null;
        Class<?> result = Toolkit.errorOrNull(() -> Class.forName(clazz.getName() + "$StyleableProperties"));
        return result == null ? findInnerClass(clazz.getSuperclass()) : result;
    }

    public static void inject(Class<? extends Styleable> styleable, CssMetaData<? extends Styleable, ?> cssMetaData) {
        Toolkit.reportErrorLater(() -> {
            Class<?> clazz = findInnerClass(styleable);
            if (clazz == null) {
                Toolkit.info("Cannot inject "+styleable+"! It doesn't have any StyleableProperties class!");
                return;
            }
            Field field = clazz.getDeclaredField("STYLEABLES");
            field.setAccessible(true);
            List<CssMetaData<? extends Styleable,?>> list = (List<CssMetaData<? extends Styleable, ?>>) field.get(null);
            clazz = Class.forName("java.util.Collections$UnmodifiableList");
            field = clazz.getDeclaredField("list");
            field.setAccessible(true);
            list = (List<CssMetaData<? extends Styleable, ?>>) field.get(list);
            list.add(cssMetaData);
        });
    }

}
