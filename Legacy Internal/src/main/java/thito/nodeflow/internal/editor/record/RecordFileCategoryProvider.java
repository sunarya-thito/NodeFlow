package thito.nodeflow.internal.editor.record;

import thito.nodeflow.api.resource.*;
import thito.nodeflow.internal.node.provider.*;

public class RecordFileCategoryProvider extends SimpleNodeProviderCategory {
    private ResourceFile file;
    public RecordFileCategoryProvider(String projectName, ResourceFile file) {
        super(projectName, file.getName(), null);
        this.file = file;
    }

    public ResourceFile getFile() {
        return file;
    }
}
