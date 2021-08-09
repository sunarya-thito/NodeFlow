package thito.nodeflow.api.bundle;

public interface Bundle {
    boolean isEnabled();

    BundleProperties getBundleProperties();

    void unload();
}
