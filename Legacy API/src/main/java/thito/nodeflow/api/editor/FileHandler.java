package thito.nodeflow.api.editor;

import thito.nodeflow.api.project.*;
import thito.nodeflow.api.resource.*;
import thito.nodeflow.api.task.*;
import thito.nodeflow.api.ui.*;

public interface FileHandler {
    String getName();
    String getExtension();
    boolean isNameValid(String name);
    Icon getIcon();
    FutureSupplier<FileSession> createSession(Project project, ProjectTab tab, ResourceFile file);
    boolean exportFile(ResourceFile file);
}
