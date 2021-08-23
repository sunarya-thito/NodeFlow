package thito.nodeflow.launcher;

import java.lang.reflect.*;
import java.net.*;

public class ApplicationClassLoader extends URLClassLoader {
    public ApplicationClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public void initialize(String[] args) throws ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> clazz = Class.forName("thito.nodeflow.internal.Bootstrap", false, this);
        Constructor<?> constructor = clazz.getConstructors()[0];
        constructor.setAccessible(true);
        constructor.newInstance(new Object[]{args});
    }
}
