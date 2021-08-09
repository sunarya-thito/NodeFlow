package thito.nodeflow.internal.editor.config;

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

public class ConfigFileSession implements FileSession {
    private UndoManagerImpl undoManager = new UndoManagerImpl();
    private ConfigFileHandler handler;
    private ResourceFile file;
    private ConfigFileUI viewport;
    private ConfigFileModule module;
    private SimpleTaskQueue queue = new SimpleTaskQueue(TaskThread.BACKGROUND);

    public ConfigFileSession(ConfigFileHandler handler, ResourceFile file, ConfigFileModule module) {
        this.handler = handler;
        this.file = file;
        this.module = module;
        module.setSession(this);
        module.getValues().addListener(new UndoableListListener<>(undoManager, I18n.$("action-variable-add"), I18n.$("action-variable-remove")));
        viewport = new ConfigFileUI(module);
    }

    public void save() {
        queue.putQuery(() -> {
            if (file instanceof WritableResourceFile) {
                try (OutputStream dos = ((WritableResourceFile) file).openOutput()) {
                    module.save(dos);
                } catch (IOException e) {
                    queue.markReady();
                    throw new ReportedError(e);
                }
            }
            queue.markReady();
        });
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
