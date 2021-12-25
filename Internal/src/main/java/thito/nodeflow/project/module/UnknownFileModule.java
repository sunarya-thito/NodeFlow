package thito.nodeflow.project.module;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.Image;
import thito.nodeflow.language.I18n;
import thito.nodeflow.project.Project;
import thito.nodeflow.resource.Resource;
import thito.nodeflow.ui.form.*;

public class UnknownFileModule implements FileModule {

    private ObjectProperty<Image> icon = new SimpleObjectProperty<>(new Image("theme:Icons/UnknownFile.png"));

    @Override
    public I18n getDisplayName() {
        return I18n.$("module.unknown.name");
    }

    @Override
    public String getExtension() {
        return null;
    }

    @Override
    public ObjectProperty<Image> iconProperty() {
        return icon;
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
