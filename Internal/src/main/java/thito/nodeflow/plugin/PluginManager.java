package thito.nodeflow.plugin;

import com.sun.javafx.css.StyleManager;
import thito.nodeflow.annotation.BGThread;
import thito.nodeflow.annotation.IOThread;
import thito.nodeflow.annotation.UIThread;
import thito.nodeflow.config.MapSection;
import thito.nodeflow.config.Section;
import thito.nodeflow.NodeFlow;
import thito.nodeflow.language.Language;
import thito.nodeflow.plugin.event.Event;
import thito.nodeflow.plugin.event.EventHandler;
import thito.nodeflow.plugin.event.EventListener;
import thito.nodeflow.plugin.event.Listener;
import thito.nodeflow.project.ProjectHandlerFactory;
import thito.nodeflow.project.module.FileModule;
import thito.nodeflow.project.module.UnknownFileModule;
import thito.nodeflow.resource.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PluginManager {
    private static PluginManager pluginManager = new PluginManager();

    public static PluginManager getPluginManager() {
        return pluginManager;
    }

    private List<Plugin> pluginList = new ArrayList<>();
    private List<FileModule> moduleList = new ArrayList<>();
    private Map<Plugin, List<String>> styleSheetMap = new HashMap<>();
    private ArrayList<EventListener> listeners = new ArrayList<>();
    private Map<Plugin, List<ProjectHandlerFactory>> projectHandlerMap = new HashMap<>();
    private UnknownFileModule unknownFileModule;

    public List<FileModule> getModuleList() {
        return Collections.unmodifiableList(moduleList);
    }

    public Collection<Plugin> getPlugins() {
        return Collections.unmodifiableList(pluginList);
    }

    public void registerPlugin(Plugin plugin) {
        pluginList.add(plugin);
    }

    public void unregisterPlugin(Plugin plugin) {
        pluginList.remove(plugin);
        projectHandlerMap.remove(plugin);
        unregisterFileModule(plugin);
        unregisterListener(plugin);
    }

    public Map<Plugin, List<ProjectHandlerFactory>> getProjectHandlerMap() {
        return projectHandlerMap;
    }

    public void registerProjectHandlerFactory(ProjectHandlerFactory factory) {
        projectHandlerMap.computeIfAbsent(Plugin.getPlugin(factory.getClass()), plugin -> new ArrayList<>())
                .add(factory);
    }

    public void unregisterProjectHandlerFactory(ProjectHandlerFactory factory) {
        List<ProjectHandlerFactory> projectHandlerFactories = projectHandlerMap.get(Plugin.getPlugin(factory.getClass()));
        if (projectHandlerFactories != null) {
            projectHandlerFactories.remove(factory);
        }
    }

    @IOThread
    public void loadPluginLocale(Language target, Plugin plugin, InputStream inputStream) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(inputStream)) {
            MapSection section = Section.parseToMap(reader);
            // find an existing language
            Language finalTarget = target;
            target = NodeFlow.getInstance().getAvailableLanguages().stream().filter(x -> x.getCode().equals(finalTarget.getCode())).findAny().orElse(target);
            target.loadLanguage(section, plugin.getId());
        }
    }

    @BGThread
    public FileModule getModule(Resource resource) {
        for (FileModule module : moduleList) {
            if (module.acceptResource(resource)) {
                return module;
            }
        }
        return unknownFileModule;
    }

    @BGThread
    public void registerFileModule(FileModule module) {
        if (module instanceof UnknownFileModule) {
            unknownFileModule = (UnknownFileModule) module;
            return;
        }
        moduleList.add(module);
    }

    @BGThread
    public void unregisterFileModule(FileModule module) {
        moduleList.remove(module);
    }

    @BGThread
    public void unregisterFileModule(Plugin plugin) {
        moduleList.removeIf(module -> Plugin.getPlugin(module.getClass()) == plugin);
    }

    @BGThread
    public void registerListener(EventListener listener) {
        listeners.add(listener);
    }

    @BGThread
    public void unregisterListener(EventListener listener) {
        listeners.remove(listener);
    }

    @BGThread
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

    @BGThread
    public void unregisterListener(Listener listener) {
        listeners.removeIf(l -> l.getListener() == listener);
    }

    @BGThread
    public void unregisterListener(Plugin plugin) {
        listeners.removeIf(l -> l.getListener() != null && Plugin.getPlugin(l.getListener().getClass()) == plugin);
    }

    @BGThread
    public <T extends Event> T fireEvent(T event) {
        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).handleEvent(event);
        }
        return event;
    }

    @UIThread
    public void addStyleSheet(Plugin plugin, String path) {
        styleSheetMap.computeIfAbsent(plugin, x -> new ArrayList<>()).add(path);
        for (List<String> other : styleSheetMap.values()) {
            if (other.contains(path)) {
                return;
            }
        }
        StyleManager.getInstance().addUserAgentStylesheet(path);
    }

    @UIThread
    public void removeStyleSheet(Plugin plugin) {
        List<String> styleList = styleSheetMap.get(plugin);
        if (styleList != null) {
            for (String path : new ArrayList<>(styleList)) {
                removeStyleSheet(plugin, path);
            }
        }
    }

    @UIThread
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
