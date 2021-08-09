package thito.nodeflow.bundled.editor;

import thito.nodeflow.api.*;
import thito.nodeflow.api.editor.*;
import thito.nodeflow.api.project.*;
import thito.nodeflow.api.resource.*;
import thito.nodeflow.api.task.*;
import thito.nodeflow.api.ui.*;

import java.io.*;

public class YamlFileHandler implements FileHandler, Describable {
    @Override
    public String getName() {
        return "Yaml";
    }

    @Override
    public String getDescribedName() {
        return "Yaml Configuration";
    }

    @Override
    public String getExtension() {
        return "yml";
    }

    @Override
    public boolean isNameValid(String name) {
        return true;
    }

    @Override
    public Icon getIcon() {
        return Icon.icon("file-yaml");
    }

    @Override
    public boolean exportFile(ResourceFile file) {
        return true;
    }

    @Override
    public FutureSupplier<FileSession> createSession(Project project, ProjectTab tab, ResourceFile file) {
        return FutureSupplier.executeOnBackground(() -> {
            try (Reader reader = file.openReader()) {
                StringWriter writer = new StringWriter();
                char[] buffer = new char[1024 * 8];
                int len;
                while ((len = reader.read(buffer, 0, buffer.length)) != -1) {
                    writer.write(buffer, 0, len);
                }
                return new YamlFileSession(this, file, writer.toString());
            } catch (Throwable t) {
                throw new ReportedError(t);
            }
        });
    }
}
