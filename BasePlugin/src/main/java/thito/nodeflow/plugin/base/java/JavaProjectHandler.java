package thito.nodeflow.plugin.base.java;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import thito.nodeflow.internal.plugin.Plugin;
import thito.nodeflow.internal.plugin.ProjectHandler;
import thito.nodeflow.internal.plugin.ProjectHandlerRegistry;

import java.util.ArrayList;
import java.util.List;

public class JavaProjectHandler extends ProjectHandler {
    private ObservableList<JavaDependency> dependencies = FXCollections.observableArrayList();
    private List<Class<?>> compiledClassList = new ArrayList<>();

    public JavaProjectHandler(Plugin plugin, ProjectHandlerRegistry registry) {
        super(plugin, registry);
    }

    public void compileStructure() {
        compiledClassList.clear();
    }

    public Class<?> findClass(String name) {
        for (JavaDependency d : dependencies) {
            Class<?> found = d.findClass(name);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    public Class<?> findClassOrDefault(String name, Class<?> def) {
        Class<?> found = findClass(name);
        return found == null ? def : found;
    }

    public ObservableList<JavaDependency> getDependencies() {
        return dependencies;
    }
}
