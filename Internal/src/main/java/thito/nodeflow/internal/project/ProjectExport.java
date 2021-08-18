package thito.nodeflow.internal.project;

import thito.nodeflow.library.language.*;

public interface ProjectExport {
    I18n getDisplayName();
    Handler createHandler(Project project);
    interface Handler {
        ProjectExport getExporter();
        ExportStatus beginExport();
    }
}
