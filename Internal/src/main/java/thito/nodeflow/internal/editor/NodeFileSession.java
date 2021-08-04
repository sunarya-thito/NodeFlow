package thito.nodeflow.internal.editor;

import javafx.beans.*;
import javafx.beans.property.*;
import javafx.collections.*;
import thito.nodeflow.api.*;
import thito.nodeflow.api.editor.*;
import thito.nodeflow.api.editor.menu.*;
import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.api.locale.*;
import thito.nodeflow.api.node.NodeLinkStyle;
import thito.nodeflow.api.project.*;
import thito.nodeflow.api.resource.*;
import thito.nodeflow.api.task.*;
import thito.nodeflow.internal.*;
import thito.nodeflow.internal.editor.menu.*;
import thito.nodeflow.internal.node.*;
import thito.nodeflow.internal.node.listener.*;
import thito.nodeflow.internal.node.property.*;
import thito.nodejfx.*;

import java.io.*;

public class NodeFileSession implements FileSession {
    private ResourceFile file;
    private NodeFileHandler handler;
    private UndoManagerImpl undoManager = new UndoManagerImpl();
    private Toolbar toolbar;
    private StandardNodeModule module;
    private NodeFileViewportImpl viewport;
    private BooleanProperty alwaysAnimate = new SimpleBooleanProperty(false);
    private ObjectProperty<NodeLinkStyle> style = new SimpleObjectProperty<>(NodeLinkStyle.CURVE);
    private ObservableSet<ModuleMember> selected = FXCollections.observableSet();
    private SimpleTaskQueue queue = new SimpleTaskQueue(TaskThread.BACKGROUND);
    private Project project;

    public NodeFileSession(Project project, ProjectTab tab, ResourceFile file, NodeFileHandler handler, StandardNodeModule module) {
        undoManager.setPaused(true);
        this.project = project;
        this.module = module;
        module.setSession(this);
        this.file = file;
        this.handler = handler;
        viewport = new NodeFileViewportImpl(project, this);
        ToolRadio groupMode, anim, snap;
        toolbar = new ToolbarImpl(this,
                DefaultToolbar.undoButton(this),
                DefaultToolbar.redoButton(this),
                new ToolSeparatorImpl(),
                DefaultToolbar.cut(selected),
                DefaultToolbar.copy(selected),
                DefaultToolbar.paste(selected, module),
                new ToolSeparatorImpl(),
                DefaultToolbar.deleteObject(selected),
                new ToolSeparatorImpl(),
                DefaultToolbar.selectMode(this).select(),
                groupMode = DefaultToolbar.groupMode(this),
                new ToolSeparatorImpl(),
                DefaultToolbar.linkStyle(style),
                anim = DefaultToolbar.showAnimation(this),
                snap = DefaultToolbar.snapToGrid(this)
        );
        undoManager.setPaused(false);
        module.nodes().addListener(this::saveObs);
        module.groups().addListener(this::saveObs);
        module.links().addListener(this::saveObs);
        module.nodes().addListener(new NodeSetListener(this, getUndoManager(), I18n.$("action-add-node"), I18n.$("action-remove-node")));
        module.links().addListener(new UndoableSetListener(getUndoManager(), I18n.$("action-link"), I18n.$("action-unlink")));
        module.groups().addListener(new UndoableSetListener(getUndoManager(), I18n.$("action-add-group"), I18n.$("action-remove-group")));
        selected.addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                tab.getTabProperties().clear();
                if (selected.size() == 1) {
                    selected.stream().findFirst().ifPresent(member -> {
                        tab.getTabProperties().setAll(ModuleMemberProperties.createProperties(member));
                    });
                }
            }
        });
        ModuleEditorState state = module.getEditorState();
        if (!"select".equals(state.getMode())) {
            groupMode.select();
        }
        groupMode.impl_selectedProperty().addListener((obs, old, val) -> {
            if (val) {
                state.setMode("group");
            } else {
                state.setMode("select");
            }
            save();
        });
        String style = state.getNodeLinkStyle();
        if (style != null) {
            try {
                styleProperty().set(NodeLinkStyle.valueOf(style));
            } catch (Throwable t) {
            }
        }
        styleProperty().addListener((obs, old, val) -> {
            state.setNodeLinkStyle(val.name());
            save();
        });
        snap.impl_selectedProperty().set(state.isSnapToGrid());
        anim.impl_selectedProperty().set(state.isPlayAnimation());
        snap.impl_selectedProperty().addListener((obs, old, val) -> {
            state.setSnapToGrid(val);
            save();
        });
        anim.impl_selectedProperty().addListener((obs, old, val) -> {
            state.setPlayAnimation(val);
            save();
        });
    }

    public Project getProject() {
        return project;
    }

    public StandardNodeModule getModule() {
        return module;
    }

    public BooleanProperty alwaysAnimateProperty() {
        return alwaysAnimate;
    }

    public void save() {
        queue.putQuery(() -> {
            if (file instanceof WritableResourceFile) {
                try (OutputStream dos = ((WritableResourceFile) file).openOutput()) {
                    module.saveAs(dos);
                } catch (IOException e) {
                    queue.markReady();
                    throw new ReportedError(e);
                }
            }
            queue.markReady();
        });
    }
    public void saveObs(Observable observable) {
        save();
    }

    public ObservableSet<ModuleMember> getSelected() {
        return selected;
    }

    public NodeLinkStyle getStyle() {
        return style.get();
    }

    public ObjectProperty<NodeLinkStyle> styleProperty() {
        return style;
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
    public NodeEditor impl_getViewport() {
        return viewport.getEditor();
    }

    @Override
    public Toolbar getToolbar() {
        return toolbar;
    }
}
