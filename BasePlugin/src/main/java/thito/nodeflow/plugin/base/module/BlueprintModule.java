package thito.nodeflow.plugin.base.module;

import thito.nodeflow.internal.project.*;
import thito.nodeflow.internal.project.module.*;
import thito.nodeflow.library.language.*;
import thito.nodeflow.library.resource.*;
import thito.nodeflow.library.ui.*;

public class BlueprintModule implements FileModule {
    @Override
    public I18n getDisplayName() {
        return I18n.$("baseplugin.blueprint.module-name");
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
}
