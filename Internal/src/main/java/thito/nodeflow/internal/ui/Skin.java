package thito.nodeflow.internal.ui;

import com.sun.javafx.css.*;
import javafx.application.*;
import javafx.beans.binding.*;
import javafx.event.*;
import javafx.scene.Node;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import org.jsoup.*;
import org.jsoup.nodes.*;

import java.lang.reflect.*;
import java.util.*;

public class Skin extends StackPane {
    private Map<String, List<EventListener<?>>> actionMap = new HashMap<>();
    private StyleSheet styleSheet;

    private Map<String, Object> idMap;

    public Skin() {
        styleSheet = ThemeManager.getInstance().getStyleSheet(this);
        Bindings.bindContent(getStylesheets(), styleSheet.getCssFiles());
        initializeSkin();
        load(requestSkinParser());
    }

    protected Window getWindow() {
        Scene scene = getScene();
        if (scene != null) {
            javafx.stage.Window peer = scene.getWindow();
            if (peer != null) {
                return (Window) peer.getProperties().get(Window.class);
            }
        }
        return null;
    }

    protected void initializeSkin() {
        registerActionHandler("window.close", ActionEvent.ACTION, event -> getWindow().close());
        registerActionHandler("window.minimize", ActionEvent.ACTION, event -> getWindow().setIconified(true));
        registerActionHandler("window.toggleMaximize", ActionEvent.ACTION, event -> {
            Window window = getWindow();
            window.setMaximized(!window.isMaximized());
        });
    }

    protected final <T extends Event> void registerActionHandler(String action, EventType<T> type, javafx.event.EventHandler<T> handler) {
        actionMap.computeIfAbsent(action, actionName -> new ArrayList<>()).add(new EventListener<>(type, handler, false));
    }

    protected final <T extends Event> void registerActionFilter(String action, EventType<T> type, javafx.event.EventHandler<T> handler) {
        actionMap.computeIfAbsent(action, actionName -> new ArrayList<>()).add(new EventListener<>(type, handler, true));
    }

    public void reload() {
        forceReloadCSS();
        load(requestSkinParser());
    }

    public void reloadCSS() {
        forceReloadCSS();
        List<String> css = new ArrayList<>(styleSheet.getCssFiles());
        styleSheet.getCssFiles().clear();
        styleSheet.getCssFiles().addAll(css);
    }

    void forceReloadCSS() {
        try {
            Field getter = Class.forName("com.sun.javafx.css.StyleManager$CacheContainer").getDeclaredField("cacheMap");
            getter.setAccessible(true);
            ((Map<Parent, ?>) StyleManager.cacheContainerMap).forEach((key, value) -> {
                try {
                    Map styleCache = (Map) getter.get(value);
                    if (styleCache != null) {
                        styleCache.clear();
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            });
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public StyleSheet getStyleSheet() {
        return styleSheet;
    }

    public void handleMenu(MenuItem item, Element layout) {
        String id = item.getId();
        if (id != null) {
            idMap.put(id, item);
        }
        if (layout.hasAttr("action")) {
            List<EventListener<?>> listenerList = actionMap.get(layout.attr("action"));
            if (listenerList == null) throw new NullPointerException("invalid action "+layout.attr("action"));
            for (EventListener listener : listenerList) {
                if (listener.asFilter) {
                    throw new UnsupportedOperationException("event filter is not available on MenuItem");
                } else {
                    item.addEventHandler(listener.type, listener.handler);
                }
            }
        }
    }

    public void handleNode(Node node, Element layout) {
        String id = node.getId();
        if (id != null) {
            idMap.put(id, node);
        }
        node.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            node.requestFocus();
        });
        if (layout.hasAttr("action")) {
            List<EventListener<?>> listenerList = actionMap.get(layout.attr("action"));
            if (listenerList == null) throw new NullPointerException("invalid action "+layout.attr("action"));
            for (EventListener listener : listenerList) {
                if (listener.asFilter) {
                    node.addEventFilter(listener.type, listener.handler);
                } else {
                    node.addEventHandler(listener.type, listener.handler);
                }
            }
        }
    }

    protected SkinParser requestSkinParser() {
        return new SkinParser(this);
    }

    protected void onLayoutUnloaded() {}
    protected void onLayoutLoaded() {}

    protected synchronized void load(SkinParser parser) {
        idMap = new HashMap<>();
        ThemeManager.getInstance().setSheetContents(ThemeManager.getInstance().getTheme(), styleSheet, getClass());
        getChildren().clear();
        Document document = Jsoup.parse(styleSheet.layoutProperty().get());
        Element components = document.selectFirst("nodeflow > components");
        parser.loadComponents(components);
        Element rootElement = document.selectFirst("nodeflow > layout > *");
        Node root = parser.createNode(rootElement);
        if (!(root instanceof Parent)) {
            idMap = null;
            throw new IllegalStateException("not a parent");
        }
        onLayoutUnloaded();
        getChildren().add(root);
        handleNode(root, rootElement);
        for (Field field : getClass().getDeclaredFields()) {
            field.setAccessible(true);
            Component component = field.getAnnotation(Component.class);
            if (component != null) {
                String id = component.value();
                Object lookup = idMap.get(id);
                if (lookup == null) throw new NullPointerException("unknown node "+id+" on field "+field.toGenericString());
                try {
                    field.set(this, lookup);
                } catch (IllegalAccessException ignored) {
                    ignored.printStackTrace();
                }
            }
        }
        for (Method method : getClass().getDeclaredMethods()) {
            method.setAccessible(true);
            if (method.getParameterCount() == 1 && Event.class.isAssignableFrom(method.getParameterTypes()[0])) {
                thito.nodeflow.internal.ui.EventHandler handler = method.getAnnotation(thito.nodeflow.internal.ui.EventHandler.class);
                if (handler != null) {
                    String id = handler.id();
                    Node node = (Node) idMap.get(id);
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
                    } else throw new NullPointerException("unknown node "+id+" on method "+method.toGenericString());
                }
                EventFilter filter = method.getAnnotation(EventFilter.class);
                if (filter != null) {
                    String id = filter.id();
                    Node node = (Node) idMap.get(id);
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
                    } else throw new NullPointerException("unknown node "+id+" on method "+method.toGenericString());
                }
            }
        }
        Platform.runLater(this::onLayoutLoaded);
        idMap = null;
    }

//    public ObjectProperty<Parent> rootProperty() {
//        return root;
//    }

    private class EventListener<T extends Event> {
        private EventType<T> type;
        private javafx.event.EventHandler<T> handler;
        private boolean asFilter;

        public EventListener(EventType<T> type, javafx.event.EventHandler<T> handler, boolean asFilter) {
            this.type = type;
            this.handler = handler;
            this.asFilter = asFilter;
        }
    }

}
