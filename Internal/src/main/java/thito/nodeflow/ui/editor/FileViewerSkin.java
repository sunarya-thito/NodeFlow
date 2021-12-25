package thito.nodeflow.ui.editor;

import javafx.beans.property.*;
import javafx.scene.layout.*;
import thito.nodeflow.project.ProjectContext;
import thito.nodeflow.project.module.*;
import thito.nodeflow.resource.Resource;
import thito.nodeflow.task.TaskThread;
import thito.nodeflow.ui.Component;
import thito.nodeflow.ui.Skin;

import java.io.IOException;
import java.io.InputStream;

public class FileViewerSkin extends Skin {

    private ObjectProperty<FileViewer> fileViewer = new SimpleObjectProperty<>();

    @Component("content")
    BorderPane content;

    Resource resource;
    FileModule module;
    ProjectContext projectContext;

    public FileViewerSkin(Resource resource, FileModule module, ProjectContext projectContext) {
        this.resource = resource;
        this.module = module;
        this.projectContext = projectContext;
    }

    @Override
    protected void onLayoutLoaded() {
        content.setCenter(new LoadingTabSkin());
        fileViewer.addListener((obs, old, val) -> {
            content.setCenter(val.getNode());
        });
        TaskThread.IO().schedule(() -> {
            try (InputStream inputStream = resource.openInput()) {
                long size = resource.getSize();
                // 0 array-copy IO
                byte[] buffer = new byte[(int) resource.getSize()];
                if (buffer.length != size) throw new IOException("file too large");
                int length = inputStream.read(buffer);
                if (length != size) throw new IOException("invalid file buffer size"); // developer error?
                TaskThread.UI().schedule(() -> {
                    FileViewer viewer = module.createViewer(projectContext.getProject(), resource);
                    viewer.reload(buffer);
                    fileViewer.set(viewer);
                });
            } catch (Throwable e) {
                TaskThread.UI().schedule(() -> {
                    content.setCenter(new ErrorTabSkin(e));
                });
                e.printStackTrace();
            }
        });
    }
}
