package thito.nodeflow.internal.bundle.java;

import thito.nodeflow.api.*;
import thito.nodeflow.api.bundle.*;
import thito.nodeflow.api.bundle.java.*;
import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.api.editor.node.java.*;
import thito.nodeflow.api.resource.*;
import thito.nodeflow.internal.bundle.*;
import thito.nodeflow.internal.node.*;
import thito.nodeflow.internal.node.provider.*;

import java.util.*;
import java.util.stream.*;

public class RuntimeJavaBundleImpl extends BundleImpl implements JavaBundle, RuntimeJavaBundle {

    public static RuntimeJavaBundleImpl createGenerated() {
        return new RuntimeJavaBundleImpl(UUID.randomUUID().toString(), "Generated", "Generated", "Generated", "Generated",
                null, null);
    }
    private Set<NodeProviderCategory> classes = new HashSet<>();
    private BundleClassLoader loader;
    private boolean shaded;
    public RuntimeJavaBundleImpl(String id, String name, String author, String description, String version, ResourceDirectory directory, BundleClassLoader classLoader) {
        super(new BundlePropertiesImpl((BundleManagerImpl) NodeFlow.getApplication().getBundleManager(),
                id, name, author, description, version, null, 0, directory) {
            @Override
            public Bundle loadBundle() {
                throw new UnsupportedOperationException();
            }
        }, NodeFlow.getApplication().getBundleManager());
        loader = classLoader;
    }

    @Override
    public boolean isShaded() {
        return shaded;
    }

    @Override
    public void setShaded(boolean shaded) {
        this.shaded = shaded;
    }

    @Override
    public void clearClasses() {
        classes.clear();
        update();
    }

    private void update() {
        if (NodeFlow.getApplication().getBundleManager().getLoadedBundles().contains(this)) {
            if (classes.isEmpty()) {
                ((BundleManagerImpl) NodeFlow.getApplication().getBundleManager()).removeBundle(this);
            }
        } else {
            if (!classes.isEmpty()) {
                ((BundleManagerImpl) NodeFlow.getApplication().getBundleManager()).addBundle(this);
            }
        }
    }

    @Override
    public void addClass(Class<?> clazz) {
        JavaNodeProviderCategory category;
        classes.add(category = new JavaNodeProviderCategory(clazz));
        ModuleManagerImpl.getInstance().registerCategory(category);
        update();
    }

    @Override
    public void removeClass(Class<?> clazz) {
        classes.removeIf(x -> {
            if (((JavaProviderCategory) x).getType() == clazz) {
                ModuleManagerImpl.getInstance().unregisterCategory(x);
                return true;
            }
            return false;
        });
        update();
    }

    @Override
    public Set<Class<?>> getClasses() {
        return classes.stream().map(x -> ((JavaProviderCategory) x).getType()).collect(Collectors.toSet());
    }

    @Override
    public Set<String> getAvailableClasses() {
        return classes.stream().map(x -> ((JavaProviderCategory) x).getType().getName()).collect(Collectors.toSet());
    }

    @Override
    public Class<?> findClass(String name) {
        Optional<NodeProviderCategory> category = classes.stream().filter(x -> ((JavaProviderCategory) x).getType().getName().equals(name)).findFirst();
        return category.isPresent() ? ((JavaProviderCategory) category.get()).getType() : null;
    }

    @Override
    public BundleClassLoader getClassLoader() {
        return loader;
    }

}
