package thito.nodeflow.internal.project;

import thito.nodeflow.internal.language.*;

public interface ProjectExport {
    I18n getDisplayName();
    Handler createHandler(Project project);
    interface Handler {
        ProjectExport getExporter();
        ExportStatus beginExport();
    }
}
