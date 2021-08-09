package thito.nodeflow.internal.project;

import thito.nodeflow.api.editor.*;
import thito.nodeflow.api.locale.*;
import thito.nodeflow.api.project.*;
import thito.nodeflow.api.resource.*;
import thito.nodeflow.api.task.*;
import thito.nodeflow.api.ui.*;

import java.util.*;

public class UnknownFacet implements ProjectFacet {

    private String id;

    public UnknownFacet(String id) {
        this.id = id;
    }

    @Override
    public Image getIcon() {
        return null;
    }

    @Override
    public String getName() {
        return I18n.$("project-props-no-facet").getString(id);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public FileHandler getFileHandler(Project project, ResourceFile file) {
        return null;
    }

    @Override
    public FacetCompilerSession createCompiler() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UnknownFacet that = (UnknownFacet) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean hasDebugSupport() {
        return false;
    }

    @Override
    public ProjectDebugger runDebugger(Project project, DebuggerListener listener, TaskThread thread, ResourceFile binary) {
        return null;
    }
}
