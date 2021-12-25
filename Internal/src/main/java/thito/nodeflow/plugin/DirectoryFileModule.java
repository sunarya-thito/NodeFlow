package thito.nodeflow.plugin;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.Image;
import thito.nodeflow.language.I18n;
import thito.nodeflow.project.Project;
import thito.nodeflow.project.module.*;
import thito.nodeflow.resource.Resource;
import thito.nodeflow.resource.ResourceType;
import thito.nodeflow.ui.Theme;
import thito.nodeflow.ui.form.*;

public class DirectoryFileModule implements FileModule {

    private ObjectProperty<Image> icon = new SimpleObjectProperty<>(new Image("theme:Icons/Folder.png"));
    @Override
    public I18n getDisplayName() {
        return I18n.$("directory");
    }

    @Override
    public ObjectProperty<Image> iconProperty() {
        return icon;
    }

    @Override
    public String getExtension() {
        return null;
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
        return Validator.resourceMustNotExist();
    }

    @Override
    public void createFile(Resource resource) {
        resource.toFile().mkdirs();
    }
}
