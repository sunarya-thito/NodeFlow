package thito.nodeflow.internal.editor.record;

import thito.nodeflow.api.*;
import thito.nodeflow.api.bundle.*;
import thito.nodeflow.api.bundle.java.*;

import java.util.*;

public class RecordFileManager {
    private static final RecordFileManager manager = new RecordFileManager();

    public static RecordFileManager getManager() {
        return manager;
    }

    public List<String> fetchTypes() {
        List<String> types = new ArrayList<>();
        for (Bundle bundle : NodeFlow.getApplication().getBundleManager().getLoadedBundles()) {
            if (bundle instanceof JavaBundle) {
                types.addAll(((JavaBundle) bundle).getAvailableClasses());
            }
        }
        return types;
    }
}
