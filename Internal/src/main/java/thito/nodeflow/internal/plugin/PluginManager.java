package thito.nodeflow.internal.plugin;

import com.sun.javafx.css.*;
import thito.nodeflow.internal.plugin.event.EventListener;
import thito.nodeflow.internal.plugin.event.*;
import thito.nodeflow.internal.project.*;
import thito.nodeflow.internal.project.module.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.logging.*;

public class PluginManager {
    private List<ProjectExport> exporter = new ArrayList<>();
    private List<FileModule> moduleList = new ArrayList<>();
    private Map<Plugin, List<String>> styleSheetMap = new HashMap<>();
    private ArrayList<EventListener> listeners = new ArrayList<>();

    public void registerListener(EventListener listener) {
        listeners.add(listener);
    }

    public void unregisterListener(EventListener listener) {
        listeners.remove(listener);
    }

    public void registerListener(Listener listener) {
        for (Method method : listener.getClass().getDeclaredMethods()) {
            method.setAccessible(true);
            EventHandler handler = method.getAnnotation(EventHandler.class);
            if (handler != null && method.getParameterCount() == 1 && Event.class.isAssignableFrom(method.getParameterTypes()[0])) {
                int index = 0;
                for (int i = 0; i < listeners.size(); i++) {
                    if (listeners.get(i).getPriority() == handler.priority()) {
                        index = i;
                        break;
                    }
                }
                listeners.add(index, new EventListener(listener, handler.priority(), handler.ignoreCancelled()) {
                    @Override
                    protected void handle(Event event) {
                        try {
                            method.invoke(listener, event);
                        } catch (IllegalAccessException e) {
                            Logger.getLogger(listener.getClass().getName()).log(Level.SEVERE, "Failed to access listener");
                        } catch (InvocationTargetException e) {
                            Logger.getLogger(listener.getClass().getName()).log(Level.SEVERE, "Failed to execute listener", e.getCause());
                        }
                    }
                });
            }
        }
    }

    public void unregisterListener(Listener listener) {
        listeners.removeIf(l -> l.getListener() == listener);
    }

    public <T extends Event> T fireEvent(T event) {
        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).handleEvent(event);
        }
        return event;
    }

    public void addStyleSheet(Plugin plugin, String path) {
        styleSheetMap.computeIfAbsent(plugin, x -> new ArrayList<>()).add(path);
        StyleManager.getInstance().addUserAgentStylesheet(path);
    }

    public void removeStyleSheet(Plugin plugin) {
        List<String> styleList = styleSheetMap.get(plugin);
        if (styleList != null) {
            for (String path : new ArrayList<>(styleList)) {
                removeStyleSheet(plugin, path);
            }
        }
    }

    public void removeStyleSheet(Plugin plugin, String path) {
        List<String> styleList = styleSheetMap.get(plugin);
        if (styleList != null) {
            if (styleList.remove(path)) {
                if (styleList.isEmpty()) styleSheetMap.remove(plugin);
                for (List<String> other : styleSheetMap.values()) {
                    if (other.contains(path)) {
                        return;
                    }
                }
                StyleManager.getInstance().removeUserAgentStylesheet(path);
            }
        }
    }

}
