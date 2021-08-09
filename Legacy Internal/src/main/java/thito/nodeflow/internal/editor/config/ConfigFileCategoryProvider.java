package thito.nodeflow.internal.editor.config;

import thito.nodeflow.api.resource.*;
import thito.nodeflow.internal.node.provider.*;

public class ConfigFileCategoryProvider extends SimpleNodeProviderCategory {
    private ResourceFile file;
    public ConfigFileCategoryProvider(String projectName, ResourceFile file) {
        super(projectName, file.getName(), null);
        this.file = file;
    }

    public ResourceFile getFile() {
        return file;
    }

}
