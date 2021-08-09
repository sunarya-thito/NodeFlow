package thito.nodeflow.api.project;

import thito.nodeflow.api.editor.*;
import thito.nodeflow.api.resource.*;
import thito.nodeflow.api.task.*;
import thito.nodeflow.api.ui.*;

public interface ProjectFacet {
    Image getIcon(); // uses Image for idk

    String getName();

    String getId();

    FileHandler getFileHandler(Project project, ResourceFile file);

    FacetCompilerSession createCompiler();

    boolean hasDebugSupport();

    ProjectDebugger runDebugger(Project project, DebuggerListener listener, TaskThread thread, ResourceFile binary);
}
