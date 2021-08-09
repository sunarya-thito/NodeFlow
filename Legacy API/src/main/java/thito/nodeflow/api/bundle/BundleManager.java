package thito.nodeflow.api.bundle;

import thito.nodeflow.api.bundle.java.*;
import thito.nodeflow.api.resource.ResourceFile;

import java.util.Collection;
import java.util.List;

public interface BundleManager {
    List<Bundle> getLoadedBundles();

    Bundle getLoadedBundle(String id);

    BundleProperties readBundleProperties(ResourceFile file);

    Collection<String> getEnabledBundles();

    void enableBundle(String id);

    void disableBundle(String id);

    JavaBundle getBundle(Class<?> type);

    RuntimeJavaBundle createDynamicBundle();

    default Class<?> findClass(String name) {
        for (Bundle bundle : getLoadedBundles()) {
            if (bundle instanceof JavaBundle) {
                Class<?> result = ((JavaBundle) bundle).findClass(name);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }
}