package thito.nodeflow.internal.bundle.java;

import thito.nodeflow.api.*;
import thito.nodeflow.api.bundle.*;
import thito.nodeflow.api.bundle.java.*;
import thito.nodeflow.api.resource.*;
import thito.nodeflow.internal.Toolkit;
import thito.nodeflow.internal.bundle.*;
import thito.nodeflow.internal.node.*;
import thito.nodeflow.internal.node.provider.*;
import thito.nodeflow.internal.resource.*;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.jar.*;

public class JavaBundleImpl extends BundleImpl implements JavaBundle {

    private BundleClassLoaderImpl classLoader;
    protected Set<String> classNames = new HashSet<>();
    public JavaBundleImpl(BundleProperties bundleProperties, BundleManager bundleManager) {
        super(bundleProperties, bundleManager);
        load();
        double index = 0;
        Set<String> classes = getAvailableClasses();
        int count = classes.size();
        for (String name : classes) {
            LocalProgress.setActivity("Loading "+name);
            try {
                Class<?> cx = findClass(name);
                if (cx == null || cx.isAnonymousClass() || cx.isSynthetic()) continue;
                ModuleManagerImpl.getInstance().registerCategory(new JavaNodeProviderCategory(cx));
                Toolkit.info("Class from bundle "+bundleProperties.getName()+": "+name);
            } catch (Throwable t) {
                Toolkit.warn("Failed to load class "+name+" in bundle "+bundleProperties.getName()+": "+t.getMessage());
                t.printStackTrace();
            }
            index++;
            LocalProgress.set(index / count);
        }
    }

    @Override
    public boolean isShaded() {
        return false;
    }

    @Override
    public BundleClassLoaderImpl getClassLoader() {
        return classLoader;
    }

    protected void load() {
        classNames.clear();
        List<URL> urls = new ArrayList<>();
        ResourceDirectory base = getBundleProperties().getDirectory();
        for (Resource child : base.getChildren()) {
            if (child instanceof FileResourceFileImpl && ((FileResourceFileImpl) child).getExtension().equals("jar")) {
                try {
                    urls.add(((PhysicalResource) child).getSystemPath().toUri().toURL());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                try (JarInputStream inputStream = new JarInputStream(((FileResourceFileImpl) child).openInput())) {
                    JarEntry entry;
                    while ((entry = inputStream.getNextJarEntry()) != null) {
                        String name = entry.getName();
                        if (name.toLowerCase().endsWith(".class")) {
                            int index = name.lastIndexOf('.');
                            classNames.add(name.substring(0, index).replace('/', '.'));
                        }
                        inputStream.closeEntry();
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }
        classLoader = new BundleClassLoaderImpl(this, urls.toArray(new URL[0]));
    }

    public void close() {
        try {
            classLoader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void unload() {
        close();
        super.unload();
    }

    @Override
    public Set<String> getAvailableClasses() {
        return classNames;
    }

    @Override
    public Class<?> findClass(String name) {
        Class<?> clazz = null;
        try {
            clazz = Class.forName(name, false, classLoader);
        } catch (Throwable e) {
        }
        return clazz;
    }


}
