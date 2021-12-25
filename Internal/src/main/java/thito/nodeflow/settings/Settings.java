package thito.nodeflow.settings;

import thito.nodeflow.NodeFlow;
import thito.nodeflow.config.Section;
import thito.nodeflow.plugin.PluginManager;
import thito.nodeflow.plugin.event.application.SettingsLoadEvent;
import thito.nodeflow.plugin.event.application.SettingsPreLoadEvent;
import thito.nodeflow.plugin.event.application.SettingsSaveEvent;
import thito.nodeflow.project.ProjectProperties;
import thito.nodeflow.settings.canvas.Category;
import thito.nodeflow.settings.canvas.ReflectedSettingsCategory;
import thito.nodeflow.settings.canvas.SettingsContext;
import thito.nodeflow.task.TaskThread;
import thito.nodeflow.task.batch.Batch;

import java.util.*;

public class Settings {
    private static Settings settings = new Settings();

    public static Settings getSettings() {
        return settings;
    }

    public static void loadProjectSettings(ProjectProperties project) {
        Settings settings = getSettings();
        unloadProjectSettings(project);
        for (Class<? extends SettingsCanvas> canvas : SettingsManager.getSettingsManager().getSettingsCanvasList()) {
            Category c = canvas.getAnnotation(Category.class);
            if (c != null && c.context() == SettingsContext.GLOBAL) continue;
            ReflectedSettingsCategory category = SettingsCanvas.readCanvas(canvas);
            SettingsContext context = category.getContext();
            if (context == SettingsContext.PROJECT || context == SettingsContext.ALL) {
                settings.putCategory(project, category);
            }
        }
    }

    public static void saveProjectSettings(ProjectProperties project) {
        List<SettingsCategory> categoryList = getSettings().categoryMap.get(project);
        if (categoryList != null) {
            for (SettingsCategory c : categoryList) {
                if (c.getContext() == SettingsContext.PROJECT) {
                    for (SettingsItem<?> item : c.getItems()) {
                        SettingsProperty<?> property = item.createProperty();
                        if (c.getContext() == SettingsContext.PROJECT || c.getContext() == SettingsContext.ALL) {
                            property.save(project.getConfiguration(), "settings." + c.getKey() + "." + item.getKey());
                        }
                    }
                }
            }
        }
    }

    public static void unloadProjectSettings(ProjectProperties project) {
        getSettings().categoryMap.remove(project);
    }

    private Map<ProjectProperties, List<SettingsCategory>> categoryMap = new LinkedHashMap<>();

    public void loadGlobalConfiguration(Section globalConfiguration) {
        for (Map.Entry<ProjectProperties, List<SettingsCategory>> entry : categoryMap.entrySet()) {
            ProjectProperties properties = entry.getKey();
            if (properties == null) {
                for (SettingsCategory c : entry.getValue()) {
                    for (SettingsItem<?> item : c.getItems()) {
                        SettingsProperty<?> property = item.createProperty();
                        if (!PluginManager.getPluginManager().fireEvent(new SettingsPreLoadEvent(property)).isCancelled()) {
                            property.load(globalConfiguration, c.getKey() + "." + item.getKey());
                            PluginManager.getPluginManager().fireEvent(new SettingsLoadEvent(property));
                        }
                    }
                }
//            } else {
//                for (SettingsCategory c : entry.getValue()) {
//                    for (SettingsItem<?> item : c.getItems()) {
//                        SettingsProperty<?> property = item.createProperty();
//                        if (!PluginManager.getPluginManager().fireEvent(new SettingsPreLoadEvent(property)).isCancelled()) {
//                            property.load(properties.getConfiguration(), "settings." + c.getKey() + "." + item.getKey());
//                            PluginManager.getPluginManager().fireEvent(new SettingsLoadEvent(property));
//                        }
//                    }
//                }
            }
        }
    }

    public Batch.Task saveGlobalConfiguration() {
        return Batch.execute(TaskThread.IO(), progress -> {
            Section globalConfiguration = NodeFlow.getInstance().readFromConfiguration();
            progress.append(TaskThread.BG(), px -> {
                for (Map.Entry<ProjectProperties, List<SettingsCategory>> entry : categoryMap.entrySet()) {
                    ProjectProperties properties = entry.getKey();
                    if (properties == null) {
                        for (SettingsCategory c : entry.getValue()) {
                            for (SettingsItem<?> item : c.getItems()) {
                                SettingsProperty<?> property = item.createProperty();
                                if (!PluginManager.getPluginManager().fireEvent(new SettingsSaveEvent(property)).isCancelled()) {
                                    property.save(globalConfiguration, c.getKey() + "." + item.getKey());
                                }
                            }
                        }
//            } else {
//                for (SettingsCategory c : entry.getValue()) {
//                    for (SettingsItem<?> item : c.getItems()) {
//                        SettingsProperty<?> property = item.createProperty();
//                        if (!PluginManager.getPluginManager().fireEvent(new SettingsSaveEvent(property)).isCancelled()) {
//                            property.save(properties.getConfiguration(), "settings." + c.getKey() + "." + item.getKey());
//                        }
//                    }
//                }
                    }
                }
                px.append(TaskThread.IO(), p2 -> {
                    NodeFlow.getInstance().saveToConfiguration(globalConfiguration);
                });
            });
        });
    }

    void putCategory(ProjectProperties project, SettingsCategory category) {
        if (category instanceof ReflectedSettingsCategory) ((ReflectedSettingsCategory) category).getCanvas().project = project;
        categoryMap.computeIfAbsent(project, x -> new ArrayList<>())
                .add(category);
    }

    void removeCategory(SettingsCategory c) {
        categoryMap.values().forEach(x -> x.remove(c));
    }

    public void registerSettingsCategory(SettingsCategory category) {
        SettingsContext context = category.getContext();
        if (context == SettingsContext.ALL || context == SettingsContext.GLOBAL) {
            putCategory(null, category);
        }
        if (context == SettingsContext.ALL || context == SettingsContext.PROJECT) {
            for (ProjectProperties projectProperties : NodeFlow.getInstance().workspaceProperty().get().getProjectPropertiesList()) {
                putCategory(projectProperties, category);
            }
        }
        for (SettingsCategory c : category.getSubCategories()) {
            registerSettingsCategory(c);
        }
    }

    void unregisterSettingsCanvas(Class<? extends SettingsCanvas> type) {
        for (List<SettingsCategory> list : categoryMap.values()) {
            for (int i = list.size() - 1; i >= 0; i--) {
                SettingsCategory c = list.get(i);
                if (c instanceof ReflectedSettingsCategory && ((ReflectedSettingsCategory) c).getCanvas().getClass() == type) {
                    list.remove(i);
                    for (SettingsCategory sub : c.getSubCategories()) {
                        unregisterSettingsCategory(sub);
                    }
                }
            }
        }
    }

    public void unregisterSettingsCategory(SettingsCategory category) {
        removeCategory(category);
        for (SettingsCategory c : category.getSubCategories()) {
            unregisterSettingsCategory(c);
        }
    }

    public <T extends SettingsCanvas> T getCategory(Class<T> type) {
        return getCategory(null, type);
    }

    public <T extends SettingsCanvas> T getCategory(ProjectProperties projectProperties, Class<T> type) {
        List<SettingsCategory> list = categoryMap.get(projectProperties);
        if (list != null) {
            T result = findCategory(list, type);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    static <T extends SettingsCanvas> T findCategory(Collection<? extends SettingsCategory> categories, Class<T> type) {
        for (SettingsCategory category : categories) {
            if (category instanceof ReflectedSettingsCategory && type.equals(((ReflectedSettingsCategory) category).getCanvas().getClass())) {
                return (T) ((ReflectedSettingsCategory) category).getCanvas();
            }
        }
        return null;
    }
}
