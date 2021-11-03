package thito.nodeflow.internal.project.module;

import thito.nodeflow.internal.annotation.IOThread;
import thito.nodeflow.internal.annotation.UIThread;
import thito.nodeflow.internal.language.I18n;
import thito.nodeflow.internal.project.Project;
import thito.nodeflow.internal.resource.Resource;
import thito.nodeflow.internal.ui.Theme;
import thito.nodeflow.internal.ui.form.Validator;

public interface FileModule {
    I18n getDisplayName();
    String getIconURL(Theme theme);
    String getExtension();
    boolean acceptResource(Resource resource);
    @UIThread // must be done in UI Thread!
    FileViewer createViewer(Project project, Resource resource);
    Validator<Resource> getFileValidator();
    @IOThread // must be done in IO Thread!
    void createFile(Resource resource);
}
