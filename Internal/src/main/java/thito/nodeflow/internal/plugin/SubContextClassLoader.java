package thito.nodeflow.internal.plugin;

import java.io.*;
import java.net.*;

public class SubContextClassLoader extends URLClassLoader {
    public SubContextClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public Class<?> transferClass(Class<?> other) {
        try (InputStream inputStream = other.getClassLoader().getResourceAsStream(other.getName().replace('.', '/')+".class")) {
            byte[] buffer = inputStream.readAllBytes();
            return defineClass(other.getName(), buffer, 0, buffer.length);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }
}
