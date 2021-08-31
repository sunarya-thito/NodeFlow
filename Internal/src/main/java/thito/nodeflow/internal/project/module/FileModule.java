package thito.nodeflow.internal.project.module;

import thito.nodeflow.internal.project.*;
import thito.nodeflow.library.language.*;
import thito.nodeflow.library.resource.*;
import thito.nodeflow.library.ui.*;

public interface FileModule {
    I18n getDisplayName();
    String getIconURL(Theme theme);
    boolean acceptResource(Resource resource);
    FileViewer createViewer(Project project, Resource resource);
}
