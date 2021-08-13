package thito.nodeflow.library.ui;

import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.event.*;
import javafx.scene.*;

import java.lang.reflect.*;

public class Skin {
    private ObjectProperty<Parent> root = new SimpleObjectProperty<>();
    private StyleSheet styleSheet;

    public Skin() {
        styleSheet = ThemeManager.getInstance().getStyleSheet(this);
        styleSheet.layoutProperty().addListener((obs, old, val) -> {
            load(val, requestSkinParser());
        });
        load(styleSheet.layoutProperty().get(), requestSkinParser());
    }

    public StyleSheet getStyleSheet() {
        return styleSheet;
    }

    protected SkinParser requestSkinParser() {
        return new SkinParser();
    }

    protected void onLayoutUnloaded() {}
    protected void onLayoutLoaded() {}

    protected void load(String html, SkinParser parser) {
        Node root = parser.load(html);
        if (!(root instanceof Parent)) throw new IllegalStateException("not a parent");
        onLayoutUnloaded();
        Bindings.unbindContent(this.root, styleSheet.getCssFiles());
        this.root.set((Parent) root);
        for (Field field : getClass().getDeclaredFields()) {
            field.setAccessible(true);
            Component component = field.getAnnotation(Component.class);
            if (component != null) {
                String id = component.value().replace("${field_name}", field.getName());
                try {
                    field.set(this, root.lookup("#"+id));
                } catch (IllegalAccessException ignored) {
                }
            }
        }
        for (Method method : getClass().getDeclaredMethods()) {
            method.setAccessible(true);
            if (method.getParameterCount() == 1 && Event.class.isAssignableFrom(method.getParameterTypes()[0])) {
                EventHandler handler = method.getAnnotation(EventHandler.class);
                if (handler != null) {
                    String id = handler.id();
                    Node node = root.lookup(id);
                    if (node != null) {
                        node.addEventHandler(handler.event().getType(), event -> {
                            try {
                                method.invoke(this, event);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            } catch (InvocationTargetException e) {
                                throw new RuntimeException(e.getCause());
                            }
                        });
                    }
                }
                EventFilter filter = method.getAnnotation(EventFilter.class);
                if (filter != null) {
                    String id = filter.id();
                    Node node = root.lookup(id);
                    if (node != null) {
                        node.addEventFilter(filter.event().getType(), event -> {
                            try {
                                method.invoke(this, event);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            } catch (InvocationTargetException e) {
                                throw new RuntimeException(e.getCause());
                            }
                        });
                    }
                }
            }
        }
        Bindings.bindContent(this.root.get().getStylesheets(), styleSheet.getCssFiles());
        onLayoutLoaded();
    }

    public ObjectProperty<Parent> rootProperty() {
        return root;
    }
}
