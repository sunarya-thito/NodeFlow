package thito.nodeflow.plugin.base.module;

import thito.nodeflow.internal.project.*;
import thito.nodeflow.internal.project.module.*;
import thito.nodeflow.internal.language.*;
import thito.nodeflow.internal.resource.*;
import thito.nodeflow.internal.ui.*;
import thito.nodeflow.internal.ui.form.*;

public class BlueprintModule implements FileModule {
    @Override
    public I18n getDisplayName() {
        return I18n.$("plugin.blueprint.module-name");
    }

    @Override
    public String getIconURL(Theme theme) {
        return "plugin:BlueprintIcon.png";
    }

    @Override
    public boolean acceptResource(Resource resource) {
        return "bpx".equalsIgnoreCase(resource.getExtension());
    }

    @Override
    public FileViewer createViewer(Project project, Resource resource) {
        return null;
    }

    @Override
    public Validator<Resource> getFileValidator() {
        return null;
    }

    @Override
    public void createFile(Resource resource) {

    }
}
