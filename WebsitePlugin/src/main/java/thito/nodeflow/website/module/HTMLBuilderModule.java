package thito.nodeflow.website.module;

import thito.nodeflow.internal.language.*;
import thito.nodeflow.internal.project.*;
import thito.nodeflow.internal.project.module.*;
import thito.nodeflow.internal.resource.*;
import thito.nodeflow.internal.ui.*;
import thito.nodeflow.internal.ui.form.*;

public class HTMLBuilderModule implements FileModule {
    @Override
    public I18n getDisplayName() {
        return null;
    }

    @Override
    public String getIconURL(Theme theme) {
        return null;
    }

    @Override
    public boolean acceptResource(Resource resource) {
        return "html".equalsIgnoreCase(resource.getExtension()) ||
                "htm".equalsIgnoreCase(resource.getExtension());
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
