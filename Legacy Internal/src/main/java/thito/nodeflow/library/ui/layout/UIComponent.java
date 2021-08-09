package thito.nodeflow.library.ui.layout;

import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.scene.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import thito.nodeflow.library.ui.*;

import java.lang.reflect.*;
import java.util.*;

public abstract class UIComponent extends StackPane {

    private BooleanProperty hovered = new SimpleBooleanProperty();
    private ObjectProperty<Layout> layout = new SimpleObjectProperty<>();
    Map<String, ObjectProperty<Node>> fieldMap;

    public UIComponent() {
        this.layout.addListener(this::onLayoutChange);
    }

    void fillOutFields() {
        if (fieldMap != null) return;
        fieldMap = new HashMap<>();
        for (Field field : getClass().getDeclaredFields()) {
            field.setAccessible(true);
            Component component = field.getAnnotation(Component.class);
            if (component != null && ObjectProperty.class.isAssignableFrom(field.getType())) {
                try {
                    String name = component.value();
                    // Variables
                    name = name.replace("${field.name}", field.getName());
                    //
                    fieldMap.put(name, (ObjectProperty<Node>) field.get(this));
                } catch (Throwable t) {
                }
            }
        }
    }

    protected void onLayoutReady() {
    }

    public void reloadLayout() {
        Layout layout = getLayout();
        setLayout(null);
        setLayout(layout);
    }

    void onLayoutChange(Observable observable, Layout old, Layout value) {
        if (value != null) {
            try {
                fillOutFields();
                value.getParser().parseLayout(value.getDocument(), this);
                fieldMap = null; // memory-clean
                onLayoutReady();
            } catch (LayoutParserException e) {
                e.printStackTrace();
            }
        } else {
            getChildren().clear();
        }
    }

    protected void addComponent(Node node) {
        getChildren().add(node);
        setup(node);
    }

    protected void setup(Node node) {
        node.addEventHandler(MouseEvent.MOUSE_CLICKED, this::onClick);
        node.addEventHandler(MouseEvent.MOUSE_ENTERED, this::onHover);
        node.addEventHandler(MouseEvent.MOUSE_EXITED, this::onUnhover);
        node.addEventHandler(MouseEvent.MOUSE_PRESSED, this::onPress);
        node.addEventHandler(MouseEvent.MOUSE_RELEASED, this::onRelease);
        node.addEventHandler(KeyEvent.KEY_PRESSED, this::onPress);
        node.addEventHandler(KeyEvent.KEY_TYPED, this::onType);
        node.addEventHandler(KeyEvent.KEY_RELEASED, this::onRelease);
        Pseudos.install(node, Pseudos.HOVERED, hovered);
    }

    protected void initializeComponent(Node component) {
        ObjectProperty<Node> property = fieldMap.get(component.getId());
        if (property != null) {
            property.set(component);
        }
    }

    // Defaults Implementable
    protected void onClick(MouseEvent event) {}
    protected void onHover(MouseEvent event) {
        hovered.set(true);
    }
    protected void onUnhover(MouseEvent event) {
        hovered.set(false);
    }
    protected void onPress(MouseEvent event) {}
    protected void onRelease(MouseEvent event) {}
    protected void onPress(KeyEvent event) {}
    protected void onType(KeyEvent event) {}
    protected void onRelease(KeyEvent event) {}
    //

    // Properties

    public boolean isHovered() {
        return hovered.get();
    }

    public BooleanProperty hoveredProperty() {
        return hovered;
    }

    public void setHovered(boolean hovered) {
        this.hovered.set(hovered);
    }

    public Layout getLayout() {
        return layout.get();
    }

    public ObjectProperty<Layout> layoutProperty() {
        return layout;
    }

    public void setLayout(Layout layout) {
        this.layout.set(layout);
    }

    //

}
