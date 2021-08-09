package thito.nodeflow.internal.bundle.java;

import thito.nodeflow.api.bundle.*;
import thito.nodeflow.api.bundle.java.*;
import thito.nodeflow.internal.*;

import java.io.*;
import java.net.*;

public class BundleClassLoaderImpl extends URLClassLoader implements BundleClassLoader, DocumentedClassLoader {
    static {
        try {
            java.lang.reflect.Method method = ClassLoader.class.getDeclaredMethod( "registerAsParallelCapable" );
            if (method != null) {
                method.setAccessible(true);
                method.invoke(null);
            }
        } catch (Exception ex) {
        }
    }
    private JavaBundle bundle;
    public BundleClassLoaderImpl(JavaBundle bundle, URL[] urls) {
        super(urls, null);
        this.bundle = bundle;
    }

    @Override
    public String getDocsURL() {
        return bundle.getBundleProperties().getJavaDoc();
    }

    @Override
    public int getDocsVersion() {
        return bundle.getBundleProperties().getJavaDocVersion();
    }

    public JavaBundle getBundle() {
        return bundle;
    }

    @Override
    public Class<?> loadClass(String name, InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buff = new byte[1024 * 8];
        int len;
        while ((len = inputStream.read(buff, 0, buff.length)) != -1) {
            outputStream.write(buff, 0, len);
        }
        buff = outputStream.toByteArray();
        return defineClass(name, buff, 0, buff.length);
    }

    @Override
    public Class<?> getClass(String name) {
        try {
            return Class.forName(name, false, this);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
}
