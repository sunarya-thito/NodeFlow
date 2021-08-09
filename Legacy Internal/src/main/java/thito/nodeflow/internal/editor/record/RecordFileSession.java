package thito.nodeflow.internal.editor.record;

import javafx.scene.*;
import thito.nodeflow.api.*;
import thito.nodeflow.api.editor.*;
import thito.nodeflow.api.editor.menu.*;
import thito.nodeflow.api.locale.*;
import thito.nodeflow.api.resource.*;
import thito.nodeflow.api.task.*;
import thito.nodeflow.internal.*;
import thito.nodeflow.internal.editor.*;
import thito.nodeflow.internal.editor.menu.*;

import java.io.*;

public class RecordFileSession implements FileSession {
    private UndoManagerImpl undoManager = new UndoManagerImpl();
    private RecordFileHandler handler;
    private ResourceFile file;
    private RecordFileModule module;
    private RecordFileUI viewport;
    private SimpleTaskQueue queue = new SimpleTaskQueue(TaskThread.BACKGROUND);

    public RecordFileSession(RecordFileHandler handler, ResourceFile file, RecordFileModule module) {
        this.handler = handler;
        this.file = file;
        this.module = module;
        module.setSession(this);
        module.getItems().addListener(new UndoableListListener<>(undoManager, I18n.$("action-variable-add"), I18n.$("action-variable-remove")));
        viewport = new RecordFileUI(this);
    }

    public void save() {
        queue.putQuery(() -> {
            if (file instanceof WritableResourceFile) {
                try (OutputStream outputStream = ((WritableResourceFile) file).openOutput()) {
                    module.save(outputStream);
                } catch (Throwable t) {
                    queue.markReady();
                    throw new ReportedError(t);
                }
            }
            queue.markReady();
        });
    }

    public RecordFileModule getModule() {
        return module;
    }

    @Override
    public UndoManager getUndoManager() {
        return undoManager;
    }

    @Override
    public FileHandler getHandler() {
        return handler;
    }

    @Override
    public ResourceFile getFile() {
        return file;
    }

    @Override
    public Node impl_getViewport() {
        return viewport;
    }

    private Toolbar toolbar;
    @Override
    public Toolbar getToolbar() {
        return toolbar == null ? toolbar = new ToolbarImpl(this,
                DefaultToolbar.undoButton(this),
                DefaultToolbar.redoButton(this)
        ) : toolbar;
    }
}
