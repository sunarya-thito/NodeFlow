package thito.nodeflow.internal.ui.editor;

import javafx.scene.control.*;
import thito.nodeflow.internal.project.*;
import thito.nodeflow.internal.project.module.*;
import thito.nodeflow.internal.resource.*;
import thito.nodeflow.internal.task.*;

import java.io.*;
import java.util.Objects;

public class FileTab extends EditorTab {

    private Project project;
    private Resource resource;
    private Tab tab;
    private FileModule module;
    private Editor editor;

    public FileTab(Editor editor, Project project, Resource resource, FileModule module) {
        TaskThread.UI().checkThread();
        this.editor = editor;
        this.project = project;
        this.module = module;
        this.resource = resource;
        tab = new Tab();
        tab.setOnCloseRequest(event -> {
            event.consume();
            editor.getOpenedTabs().remove(this);
        });
        tab.getProperties().put(FileTab.class, this);
    }

    public Editor getEditor() {
        return editor;
    }

    public Resource getResource() {
        return resource;
    }

    public void reload() {
        TaskThread.UI().checkThread();
        LoadingTabSkin loading = new LoadingTabSkin();
        tab.setContent(loading);
        TaskThread.IO().schedule(() -> {
            try {
                try (FileInputStream inputStream = (FileInputStream) resource.openInput()) {
                    long sizeLong = inputStream.getChannel().size();
                    int size = (int) sizeLong;
                    if (sizeLong != size) throw new IllegalStateException("FILE TOO LARGE");
                    byte[] buffer = new byte[size];
                    int len = 0;
                    while (len < size) {
                        int totalRead = inputStream.read(buffer, len, Math.min(1024 * 8, size - len));
                        if (totalRead == -1) break;
                        len += totalRead;
                        int finalI = len;
                        TaskThread.UI().schedule(() -> {
                            loading.getProgressBar().setProgress(finalI / (double) buffer.length);
                        });
                    }
                    TaskThread.UI().schedule(() -> {
                        // Allowing the viewer to initialize their fx component
                        FileViewer fileViewer = module.createViewer(project, resource);
                        TaskThread.BACKGROUND().schedule(() -> {
                            // Parse the file into viewable components in background
                            fileViewer.reload(buffer);
                            TaskThread.UI().schedule(() -> {
                                // When done, show the viewer fx component to the user
                                tab.setContent(fileViewer.getNode());
                            });
                        });
                    });
                }
            } catch (Throwable t) {
                TaskThread.UI().schedule(() -> {
                    tab.setContent(new ErrorTabSkin(t));
                });
            }
        });
    }

    protected void updateName() {
        TabPane pane = tab.getTabPane();
        if (pane != null) {
            for (Tab other : pane.getTabs()) {
                if (other != tab && Objects.equals(other.getText(), resource.getName())) {
                    tab.setText(resource.toFile().getPath());
                    return;
                }
            }
        }
        tab.setText(resource.getName());
    }

    public Tab getTab() {
        return tab;
    }
}
