package thito.nodeflow.internal.bundle;

import thito.nodeflow.api.*;
import thito.nodeflow.api.bundle.*;
import thito.nodeflow.api.bundle.java.*;
import thito.nodeflow.api.config.*;
import thito.nodeflow.api.resource.*;
import thito.nodeflow.internal.bundle.java.*;
import thito.nodeflow.internal.config.*;

import java.util.*;
import java.util.function.*;

import static java.util.Objects.*;

public class BundleManagerImpl implements BundleManager {
    protected List<Bundle> bundleList = new ArrayList<>();
    protected Set<String> enabledBundles = new HashSet<>();

    @Override
    public List<Bundle> getLoadedBundles() {
        return Collections.unmodifiableList(bundleList);
    }

    @Override
    public RuntimeJavaBundle createDynamicBundle() {
        return RuntimeJavaBundleImpl.createGenerated();
    }

    @Override
    public BundleProperties readBundleProperties(ResourceFile file) {
        Section section = Section.loadYaml(file);
        return new BundlePropertiesImpl(this,
                requireNonNull(section.getString("id"), "id"),
                requireNonNull(section.getString("name"), "name"),
                requireNonNull(section.getString("author"), "author"),
                requireNonNull(section.getString("description"), "description"),
                requireNonNull(section.getString("version"), "version"),
                section.getString("javadoc"),
                section.getInt("javadoc-version"),
                file.getParentDirectory()
                );
    }

    @Override
    public JavaBundle getBundle(Class<?> type) {
        ClassLoader loader = type.getClassLoader();
        if (loader instanceof BundleClassLoaderImpl) {
            return ((BundleClassLoaderImpl) loader).getBundle();
        }
        return null;
    }

    @Override
    public Bundle getLoadedBundle(String id) {
        for (int i = 0; i < bundleList.size(); i++) {
            Bundle bundle = bundleList.get(i);
            if (bundle.getBundleProperties().getId().equals(id)) {
                return bundle;
            }
        }
        return null;
    }

    public void removeBundle(JavaBundle bundle) {
        bundleList.remove(bundle);
    }

    public void addBundle(JavaBundle bundle) {
        bundleList.add(bundle);
    }

    public Bundle loadBundle(BundleProperties properties) {
        if (getLoadedBundle(properties.getId()) != null) throw new ReportedError(new IllegalStateException("duplicate bundle \"" + properties.getId() + "\""));
        JavaBundleImpl bundle = new JavaBundleImpl(properties, this);
        addBundle(bundle);
        return bundle;
    }

    public void loadBundleConfiguration(ResourceFile file) {
        Section section = Section.loadYaml(file);
        enabledBundles.addAll(section.getList("enabled-bundles").filter(String.class));
    }

    public void storeBundleConfiguration(WritableResourceFile file) {
        Section section = new MapSectionImpl();
        section.set(enabledBundles, "enabled-bundles");
        Section.saveYaml(section, file);
    }

    public List<Class<?>> collectClasses(Predicate<Class<?>> predicate) {
        List<Class<?>> classes = new ArrayList<>();
        for (Bundle bundle : getLoadedBundles()) {
            if (bundle instanceof JavaBundle) {
                for (String name : ((JavaBundle) bundle).getAvailableClasses()) {
                    Class<?> cl = ((JavaBundle) bundle).findClass(name);
                    if (cl != null && predicate.test(cl)) {
                        classes.add(cl);
                    }
                }
            }
        }
        return classes;
    }

    @Override
    public Collection<String> getEnabledBundles() {
        return Collections.unmodifiableCollection(enabledBundles);
    }

    @Override
    public void enableBundle(String id) {
        enabledBundles.add(id);
    }

    @Override
    public void disableBundle(String id) {
        enabledBundles.remove(id);
    }
}
