package thito.nodeflow.internal.plugin;

import thito.nodeflow.internal.project.*;
import thito.nodeflow.internal.project.module.*;
import thito.nodeflow.library.language.*;
import thito.nodeflow.library.resource.*;
import thito.nodeflow.library.ui.*;
import thito.nodeflow.library.ui.form.*;

public class DirectoryFileModule implements FileModule {
    @Override
    public I18n getDisplayName() {
        return I18n.$("directory");
    }

    @Override
    public String getIconURL(Theme theme) {
        return null;
    }

    @Override
    public boolean acceptResource(Resource resource) {
        return resource.getType() == ResourceType.DIRECTORY;
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
