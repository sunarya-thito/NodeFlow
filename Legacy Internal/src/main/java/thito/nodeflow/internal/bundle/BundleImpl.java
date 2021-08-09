package thito.nodeflow.internal.bundle;

import thito.nodeflow.api.bundle.*;

public class BundleImpl implements Bundle {
    private final BundleProperties bundleProperties;
    private final BundleManager bundleManager;

    public BundleImpl(BundleProperties bundleProperties, BundleManager bundleManager) {
        this.bundleProperties = bundleProperties;
        this.bundleManager = bundleManager;
    }

    public boolean isEnabled() {
        return ((BundleManagerImpl) bundleManager).bundleList.contains(getBundleProperties().getId());
    }

    @Override
    public BundleProperties getBundleProperties() {
        return bundleProperties;
    }

    @Override
    public void unload() {
        ((BundleManagerImpl) bundleManager).bundleList.remove(this);
    }

}
