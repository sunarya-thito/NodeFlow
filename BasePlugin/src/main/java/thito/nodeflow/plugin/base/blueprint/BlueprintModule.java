package thito.nodeflow.plugin.base.blueprint;

import thito.nodeflow.internal.language.I18n;
import thito.nodeflow.internal.project.Project;
import thito.nodeflow.internal.project.module.FileModule;
import thito.nodeflow.internal.project.module.FileViewer;
import thito.nodeflow.internal.resource.Resource;
import thito.nodeflow.internal.ui.Theme;
import thito.nodeflow.internal.ui.form.Validator;

import java.io.IOException;

public class BlueprintModule implements FileModule {
    @Override
    public I18n getDisplayName() {
        return I18n.$("plugin.blueprint.module-name");
    }

    @Override
    public String getExtension() {
        return "jbp";
    }

    @Override
    public String getIconURL(Theme theme) {
        return "theme://blueprint/Icons/BlueprintModuleIcon.png";
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
