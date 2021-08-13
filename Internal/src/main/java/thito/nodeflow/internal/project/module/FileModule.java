package thito.nodeflow.internal.project.module;

import thito.nodeflow.library.language.*;
import thito.nodeflow.library.resource.*;
import thito.nodeflow.library.ui.*;

public interface FileModule {
    I18n getDisplayName();
    Icon getIcon();
    boolean acceptResource(Resource resource);
    FileViewer createViewer(Resource resource);
}
