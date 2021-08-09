package thito.nodeflow.bundled.editor;

import javafx.scene.*;
import org.yaml.snakeyaml.*;
import thito.nodeflow.api.*;
import thito.nodeflow.api.editor.*;
import thito.nodeflow.api.editor.menu.*;
import thito.nodeflow.api.locale.*;
import thito.nodeflow.api.resource.*;
import thito.nodeflow.internal.editor.*;
import thito.nodeflow.internal.editor.menu.*;

import java.io.*;

public class YamlFileSession implements FileSession {

    private UndoManager undoManager = new UndoManagerImpl();
    private FileHandler handler;
    private ResourceFile file;
    private YamlViewport viewport;

    public YamlFileSession(FileHandler handler, ResourceFile file, String text) {
        this.handler = handler;
        this.file = file;
        viewport = new YamlViewport(text);
        viewport.getCode().textProperty().addListener((obs, old, val) -> {
            try {
                Yaml yaml = new Yaml();
                yaml.load(val);
                undoManager.storeAction(I18n.$("action-text-type").stringBinding(), () -> {
                    viewport.getCode().replaceText(old);
                }, () -> {
                    viewport.getCode().replaceText(val);
                });
            } catch (Throwable t) {
            }
            if (file instanceof WritableResourceFile) {
                try (Writer writer = ((WritableResourceFile) file).openWriter()) {
                    writer.write(val);
                } catch (Throwable t) {
                    throw new ReportedError(t);
                }
            }
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
        return viewport.getCode();
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
