package thito.nodeflow.internal.project.module;

import thito.nodeflow.internal.project.*;
import thito.nodeflow.internal.language.*;
import thito.nodeflow.internal.resource.*;
import thito.nodeflow.internal.ui.*;
import thito.nodeflow.internal.ui.form.*;

public class UnknownFileModule implements FileModule {
    @Override
    public I18n getDisplayName() {
        return I18n.$("module.unknown.name");
    }

    @Override
    public String getExtension() {
        return null;
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
    public FileViewer createViewer(Project project, Resource resource) {
        return new UnknownFileViewer(this, project, resource);
    }

    @Override
    public Validator<Resource> getFileValidator() {
        return null;
    }

    @Override
    public void createFile(Resource resource) {
    }
}
