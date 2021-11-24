package thito.nodeflow.ui;

import javafx.beans.*;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.css.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.layout.*;

import java.lang.reflect.*;
import java.util.*;

public class AdvancedPseudoClass {

    static List<CssMetaData<? extends Styleable, ?>> NODE_STYLEABLES;
    static CssMetaData<Node, Number> fxLayoutX, fxLayoutY;
    static CssMetaData<Node, Pos> fxBorderPaneAlignment;
    static CssMetaData<Node, Number> fxFitWidth;
    static CssMetaData<Node, Number> fxFitHeight;
    static {
        try {
            inject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        NODE_STYLEABLES.add(fxLayoutX = UIHelper.meta("-fx-layout-x", StyleConverter.getSizeConverter(), null, n -> {
            AdvancedPseudoClass advancedPseudoClass = (AdvancedPseudoClass) n.getProperties().computeIfAbsent(n, x -> new AdvancedPseudoClass(n));
            return advancedPseudoClass.layoutX;
        }));
        NODE_STYLEABLES.add(fxLayoutY = UIHelper.meta("-fx-layout-y", StyleConverter.getSizeConverter(), null, n -> {
            AdvancedPseudoClass advancedPseudoClass = (AdvancedPseudoClass) n.getProperties().computeIfAbsent(n, x -> new AdvancedPseudoClass(n));
            return advancedPseudoClass.layoutY;
        }));
        NODE_STYLEABLES.add(fxBorderPaneAlignment = new CssMetaData<>("-fx-border-pane-alignment", StyleConverter.getEnumConverter(Pos.class),
                Pos.CENTER) {
            @Override
            public boolean isSettable(Node styleable) {
                return !getStyleableProperty(styleable).isBound();
            }

            @Override
            public StyleableObjectProperty<Pos> getStyleableProperty(Node styleable) {
                StyleableObjectProperty<Pos> pos = (StyleableObjectProperty<Pos>) styleable.getProperties().computeIfAbsent(fxBorderPaneAlignment, key -> {
                    StyleableObjectProperty<Pos> prop = new SimpleStyleableObjectProperty<>(fxBorderPaneAlignment, Pos.CENTER);
                    prop.addListener((obs, old, val) -> {
                        BorderPane.setAlignment(styleable, val);
                    });
                    return prop;
                });
                return pos;
            }
        });
    }

    public static void init() {}

    private static void inject() throws Exception {
        Class<?> styleablePropertiesClass = Class.forName("javafx.scene.Node$StyleableProperties");
        Field field = styleablePropertiesClass.getDeclaredField("STYLEABLES");
        field.setAccessible(true);
        List unmodifiable = (List) field.get(null);
        Class<?> unmodifiableList = Class.forName("java.util.Collections$UnmodifiableList");
        Field unmodifiableField = unmodifiableList.getDeclaredField("list");
        unmodifiableField.setAccessible(true);
        NODE_STYLEABLES = (ArrayList) unmodifiableField.get(unmodifiable);
    }

    private static final PseudoClass firstChild = PseudoClass.getPseudoClass("first");
    private static final PseudoClass lastChild = PseudoClass.getPseudoClass("last");
    private static final PseudoClass evenChild = PseudoClass.getPseudoClass("even");
    private static final PseudoClass oddChild = PseudoClass.getPseudoClass("odd");
    private ObjectProperty<PseudoClass> x = new SimpleObjectProperty<>();
    private ObjectProperty<PseudoClass> y = new SimpleObjectProperty<>();
    private ObjectProperty<PseudoClass> width = new SimpleObjectProperty<>();
    private ObjectProperty<PseudoClass> height = new SimpleObjectProperty<>();
    private StyleableDoubleProperty layoutX = new SimpleStyleableDoubleProperty(fxLayoutX);
    private StyleableDoubleProperty layoutY = new SimpleStyleableDoubleProperty(fxLayoutY);

    private Node node;

    public AdvancedPseudoClass(Node node) {
        this.node = node;
        layoutX.bindBidirectional(node.layoutXProperty());
        layoutY.bindBidirectional(node.layoutYProperty());
        node.layoutBoundsProperty().addListener((obs, old, val) -> {
            x.set(PseudoClass.getPseudoClass("x_"+(int) val.getMinX()));
            y.set(PseudoClass.getPseudoClass("y_"+(int) val.getMinY()));
            width.set(PseudoClass.getPseudoClass("width_"+(int) val.getWidth()));
            height.set(PseudoClass.getPseudoClass("height_"+(int) val.getHeight()));
        });

        registerPseudoProperty(x);
        registerPseudoProperty(y);
        registerPseudoProperty(width);
        registerPseudoProperty(height);

        if (node instanceof Parent) {
            ObservableList<Node> children = ((Parent) node).getChildrenUnmodifiable();
            children.addListener((InvalidationListener) obs -> {
                int index = 0;
                for (Node child : children) {
                    if (!child.getProperties().containsKey(AdvancedPseudoClass.class)) {
                        child.getProperties().put(AdvancedPseudoClass.class, new AdvancedPseudoClass(child));
                    }
                    if (index == 0) {
                        child.pseudoClassStateChanged(firstChild, true);
                    } else {
                        child.pseudoClassStateChanged(firstChild, false);
                    }
                    if (index == children.size() - 1) {
                        child.pseudoClassStateChanged(lastChild, true);
                    } else {
                        child.pseudoClassStateChanged(lastChild, false);
                    }
                    if (index % 2 == 0) {
                        child.pseudoClassStateChanged(evenChild, true);
                        child.pseudoClassStateChanged(oddChild, false);
                    } else {
                        child.pseudoClassStateChanged(evenChild, false);
                        child.pseudoClassStateChanged(oddChild, true);
                    }
                    index++;
                }
            });
        }
    }

    void registerPseudoProperty(ObjectProperty<PseudoClass> property) {
        property.addListener((obs, old, val) -> {
            if (old != null) node.pseudoClassStateChanged(old, false);
            if (val != null) node.pseudoClassStateChanged(val, true);
        });
    }
}
