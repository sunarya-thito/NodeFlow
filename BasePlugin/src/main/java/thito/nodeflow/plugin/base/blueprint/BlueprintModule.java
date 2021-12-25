package thito.nodeflow.plugin.base.blueprint;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.Image;
import thito.nodeflow.language.I18n;
import thito.nodeflow.project.Project;
import thito.nodeflow.project.module.FileModule;
import thito.nodeflow.project.module.FileViewer;
import thito.nodeflow.resource.Resource;
import thito.nodeflow.ui.Theme;
import thito.nodeflow.ui.form.Validator;

import java.io.IOException;

public class BlueprintModule implements FileModule {
    private ObjectProperty<Image> icon = new SimpleObjectProperty<>(new Image("theme://blueprint/Icons/BlueprintModuleIcon.png"));
    @Override
    public I18n getDisplayName() {
        return I18n.$("plugin.blueprint.module-name");
    }

    @Override
    public ObjectProperty<Image> iconProperty() {
        return icon;
    }

    @Override
    public String getExtension() {
        return "jbp";
    }

    @Override
    public boolean acceptResource(Resource resource) {
        return resource != null && "jbp".equalsIgnoreCase(resource.getExtension());
    }

    @Override
    public FileViewer createViewer(Project project, Resource resource) {
        return new BlueprintViewer(project, resource, this);
    }

    @Override
    public Validator<Resource> getFileValidator() {
        return Validator.resourceMustNotExist();
    }

    @Override
    public void createFile(Resource resource) {
        try {
            resource.toFile().createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
