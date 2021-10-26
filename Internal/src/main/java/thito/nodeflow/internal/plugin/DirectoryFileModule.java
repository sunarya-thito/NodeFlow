package thito.nodeflow.internal.plugin;

import thito.nodeflow.internal.project.*;
import thito.nodeflow.internal.project.module.*;
import thito.nodeflow.internal.language.*;
import thito.nodeflow.internal.resource.*;
import thito.nodeflow.internal.ui.*;
import thito.nodeflow.internal.ui.form.*;

public class DirectoryFileModule implements FileModule {
    @Override
    public I18n getDisplayName() {
        return I18n.$("directory");
    }

    @Override
    public String getIconURL(Theme theme) {
        return "rsrc:Themes/" + theme.getName() + "/Icons/Folder.png";
    }

    @Override
    public boolean acceptResource(Resource resource) {
        return resource != null && resource.getType() == ResourceType.DIRECTORY;
    }

    @Override
    public FileViewer createViewer(Project project, Resource resource) {
        return null;
    }

    @Override
    public Validator<Resource> getFileValidator() {
        return Validator.resourceExistValidator();
    }

    @Override
    public void createFile(Resource resource) {
        System.out.println("MAKING DIRS: "+resource.toFile());
        resource.toFile().mkdirs();
    }
}
