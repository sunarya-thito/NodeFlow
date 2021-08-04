package thito.nodeflow.internal.editor.config;

import thito.nodeflow.api.*;
import thito.nodeflow.api.editor.*;
import thito.nodeflow.api.project.*;
import thito.nodeflow.api.resource.*;
import thito.nodeflow.api.task.*;
import thito.nodeflow.api.ui.*;

import java.io.*;

public class ConfigFileHandler implements FileHandler, Describable {
    @Override
    public String getName() {
        return "Configuration";
    }

    @Override
    public String getExtension() {
        return "cfg";
    }

    @Override
    public String getDescribedName() {
        return getName();
    }

    @Override
    public Icon getIcon() {
        return Icon.icon("file-configuration");
    }

    @Override
    public boolean isNameValid(String name) {
        return true;
    }

    @Override
    public boolean exportFile(ResourceFile file) {
        return false;
    }

    @Override
    public FutureSupplier<FileSession> createSession(Project project, ProjectTab tab, ResourceFile file) {
        return FutureSupplier.executeOnBackground(() -> {
            String relativePath = new File(project.getSourceDirectory().getPath()).toPath().relativize(new File(file.getPath()).toPath()).toString();
            int index = relativePath.lastIndexOf('.');
            if (index >= 0) {
                relativePath = relativePath.substring(0, index);
            }
            ConfigFileModule module = new ConfigFileModule(relativePath);
            try (InputStream inputStream = file.openInput()) {
                module.load(inputStream);
            } catch (Throwable t) {
                throw new ReportedError(t);
            }
            return new ConfigFileSession(this, file, module);
        });
    }
}
