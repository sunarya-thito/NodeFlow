package thito.nodeflow.internal.project.module;

import thito.nodeflow.internal.project.*;
import thito.nodeflow.library.language.*;
import thito.nodeflow.library.resource.*;
import thito.nodeflow.library.ui.*;

public class UnknownFileModule implements FileModule {
    @Override
    public I18n getDisplayName() {
        return I18n.$("module.unknown.name");
    }

    @Override
    public String getIconURL(Theme theme) {
        return "rsrc:Themes/"+theme.getName()+"/Icons/UnknownFile.png";
    }

    @Override
    public boolean acceptResource(Resource resource) {
        return true;
    }

    @Override
    public FileViewer createViewer(Project project, Resource resource, byte[] data) {
        return new UnknownFileViewer(this, project, resource, data);
    }
}
