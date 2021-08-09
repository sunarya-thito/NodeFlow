package thito.nodeflow.internal.editor.record;

import thito.nodeflow.api.*;
import thito.nodeflow.api.editor.*;
import thito.nodeflow.api.project.*;
import thito.nodeflow.api.resource.*;
import thito.nodeflow.api.task.*;
import thito.nodeflow.api.ui.*;

import java.io.*;

public class RecordFileHandler implements FileHandler, Describable {
    @Override
    public String getName() {
        return "Record";
    }

    @Override
    public String getExtension() {
        return "rec";
    }

    @Override
    public Icon getIcon() {
        return Icon.icon("file-record");
    }

    @Override
    public boolean isNameValid(String name) {
        return true;
//        return SourceVersion.isIdentifier(name) && !SourceVersion.isKeyword(name);
    }

    @Override
    public boolean exportFile(ResourceFile file) {
        return false;
    }

    @Override
    public FutureSupplier<FileSession> createSession(Project project, ProjectTab tab, ResourceFile file) {
        return FutureSupplier.executeOnBackground(() -> {
            RecordFileModule module = new RecordFileModule(file.getName(), file, true);
            try (InputStream inputStream = file.openInput()) {
                module.load(inputStream);
            } catch (Throwable t) {
                throw new ReportedError(t);
            }
            return new RecordFileSession(this, file, module);
        });
    }

    @Override
    public String getDescribedName() {
        return getName();
    }
}
