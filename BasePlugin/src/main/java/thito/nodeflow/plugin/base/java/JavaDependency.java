package thito.nodeflow.plugin.base.java;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class JavaDependency {
    private File file;
    private URLClassLoader classLoader;

    public JavaDependency(File file) {
        this.file = file;
        try {
            classLoader = new URLClassLoader(new URL[] { file.toURI().toURL() });
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() throws IOException {
        classLoader.close();
    }

    public File getFile() {
        return file;
    }

    public URLClassLoader getClassLoader() {
        return classLoader;
    }

    public Class<?> findClass(String name) {
        try {
            return Class.forName(name, false, classLoader);
        } catch (Throwable t) {
            return null;
        }
    }
}
