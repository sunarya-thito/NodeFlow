package thito.nodeflow.project.module;

import javafx.beans.property.ObjectProperty;
import javafx.scene.image.Image;
import thito.nodeflow.annotation.IOThread;
import thito.nodeflow.annotation.UIThread;
import thito.nodeflow.language.I18n;
import thito.nodeflow.project.Project;
import thito.nodeflow.resource.Resource;
import thito.nodeflow.ui.Theme;
import thito.nodeflow.ui.form.Validator;

public interface FileModule {
    I18n getDisplayName();
    ObjectProperty<Image> iconProperty();
//    Image getIcon();
    String getExtension();
    boolean acceptResource(Resource resource);
    @UIThread // must be done in UI Thread!
    FileViewer createViewer(Project project, Resource resource);
    Validator<Resource> getFileValidator();
    @IOThread // must be done in IO Thread!
    void createFile(Resource resource);
}
