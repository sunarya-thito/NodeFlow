package thito.nodeflow.api.bundle;

import java.io.*;

public interface BundleClassLoader {
    Class<?> getClass(String name);
    Class<?> loadClass(String name, InputStream inputStream) throws IOException;
    Bundle getBundle();
}
