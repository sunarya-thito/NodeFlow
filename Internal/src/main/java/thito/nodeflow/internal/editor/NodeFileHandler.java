package thito.nodeflow.internal.editor;

import thito.nodeflow.api.*;
import thito.nodeflow.api.editor.*;
import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.api.project.*;
import thito.nodeflow.api.resource.*;
import thito.nodeflow.api.task.*;
import thito.nodeflow.api.ui.*;
import thito.nodeflow.internal.node.*;

import java.io.*;

public class NodeFileHandler implements FileHandler, Describable {
    @Override
    public String getName() {
        return "Node Module";
    }

    @Override
    public String getExtension() {
        return "ndx";
    }

    @Override
    public String getDescribedName() {
        return getName();
    }

    @Override
    public Icon getIcon() {
        return Icon.icon("file-module");
    }

    @Override
    public boolean isNameValid(String name) {
        return true;
    }

    @Override
    public FutureSupplier<FileSession> createSession(Project project, ProjectTab tab, ResourceFile file) {
        FutureSupplier<FileSession> fileSession = FutureSupplier.executeOnBackground(() -> {
            try {
                StandardNodeModule module = new StandardNodeModule(file.getName(), file);
                module.setProject(project);
                try (DataInputStream dis = new DataInputStream(file.openInput())) {
                    if (dis.available() <= 0) {
                        NodeFileSession session = new NodeFileSession(project, tab, file, this, module);
                        return session;
                    }
                    module.loadFrom(dis, tab.impl_ignoredErrors().contains(MissingProviderException.class));
                }
                NodeFileSession session = new NodeFileSession(project, tab, file, this, module);
                return session;
            } catch (Throwable t) {
                throw new ReportedError(t);
            }
        });
        return fileSession;
    }

    @Override
    public boolean exportFile(ResourceFile file) {
        return false;
    }
}
