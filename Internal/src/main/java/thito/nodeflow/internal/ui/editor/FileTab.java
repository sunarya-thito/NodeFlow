package thito.nodeflow.internal.ui.editor;

import javafx.scene.control.*;
import thito.nodeflow.internal.project.*;
import thito.nodeflow.internal.project.module.*;
import thito.nodeflow.library.resource.*;
import thito.nodeflow.library.task.*;

import java.io.*;

public class FileTab {

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
            editor.getOpenedFiles().remove(this);
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
                    for (int i = 0; i < buffer.length; i++) {
                        int read = inputStream.read();
                        if (read == -1) throw new IllegalStateException("UNEXPECTED END OF FILE");
                        buffer[i] = (byte) read;
                        int finalI = i;
                        TaskThread.UI().schedule(() -> {
                            loading.getProgressBar().setProgress(finalI / (double) buffer.length);
                        });
                    }
                    TaskThread.UI().schedule(() -> {
                        FileViewer fileViewer = module.createViewer(project, resource, buffer);
                        tab.setContent(fileViewer.getNode());
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
                if (other != tab && other.getText().equals(resource.getName())) {
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
