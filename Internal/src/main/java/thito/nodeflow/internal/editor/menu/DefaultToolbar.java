package thito.nodeflow.internal.editor.menu;

import javafx.beans.*;
import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.collections.*;
import thito.nodeflow.api.editor.*;
import thito.nodeflow.api.editor.menu.*;
import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.api.locale.*;
import thito.nodeflow.api.node.NodeLinkStyle;
import thito.nodeflow.api.node.*;
import thito.nodeflow.api.resource.*;
import thito.nodeflow.api.task.*;
import thito.nodeflow.api.ui.*;
import thito.nodeflow.api.ui.dialog.*;
import thito.nodeflow.api.ui.dialog.button.*;
import thito.nodeflow.internal.*;
import thito.nodeflow.internal.clipboard.*;
import thito.nodeflow.internal.editor.*;
import thito.nodeflow.internal.node.*;
import thito.nodeflow.internal.node.headless.*;
import thito.nodeflow.internal.project.*;
import thito.nodeflow.internal.resource.*;
import thito.nodeflow.internal.ui.*;
import thito.nodeflow.internal.ui.dialog.*;
import thito.nodeflow.internal.ui.editor.*;
import thito.nodeflow.internal.ui.popup.*;
import thito.nodejfx.Node;
import thito.nodejfx.NodeGroup;
import thito.nodejfx.*;

import java.io.*;
import java.util.*;

public class DefaultToolbar extends ToolbarImpl {

//    public static ToolButton saveButton(FileEditorSession editorSession) {
//        ToolButton button = new ToolButtonImpl(I18n.$("tool-save-file"), Icon.icon("save-file"), mouseButton -> {
//            editorSession.saveSession();
//            editorSession.impl_unsavedProperty().set(false);
//        });
//        button.impl_disableProperty().bind(editorSession.impl_unsavedProperty().not());
//        return button;
//    }

    public static ToolButton undoButton(FileSession node) {
        ObjectProperty<String> named = new SimpleObjectProperty<>("");
        node.getUndoManager().impl_nextUndoBinding().addListener((obs, old, val) -> {
            if (val != null) {
                named.bind(val.getDisplayName());
            }
        });
        ToolButton button = new ToolButtonImpl(I18n.$("tool-undo").format(named), Icon.icon("undo"), mouseButton -> {
            node.getUndoManager().undo();
        });
        button.impl_disableProperty().bind(node.getUndoManager().impl_hasUndoProperty().not());
        return button;
    }

    public static ToolChoice<NodeLinkStyle> linkStyle(ObjectProperty<NodeLinkStyle> chosen) {
        ToolChoiceImpl<NodeLinkStyle> tool = new ToolChoiceImpl<>(chosen, NodeLinkStyle.values());
        return tool;
    }

    public static ToolButton redoButton(FileSession node) {
        StringProperty named = new SimpleStringProperty();
        node.getUndoManager().impl_nextRedoBinding().addListener((obs, old, val) -> {
            if (val != null) {
                named.bind(val.getDisplayName());
            }
        });
        ToolButton button = new ToolButtonImpl(I18n.$("tool-redo").format(
                named
        ), Icon.icon("redo"), mouseButton -> {
            node.getUndoManager().redo();
        });
        button.impl_disableProperty().bind(node.getUndoManager().impl_hasRedoProperty().not());
        return button;
    }

    public static ToolButton deleteFileButton(EditorUI window) {
        ToolButton button = new ToolButtonImpl(I18n.$("tool-delete-file"), Icon.icon("delete-file"), mouseButton -> {
            Dialogs.deleteFile(window.getWindow(), window.getExplorer().getResourceTree().getSelectionModel().getSelectedItems().stream().map(x -> (PhysicalResource) x.getValue()).toArray(PhysicalResource[]::new));
        });
        button.impl_disableProperty().bind(window.getExplorer().getResourceTree().getSelectionModel().selectedItemProperty().isNull());
        return button;
    }

    public static ToolButton deleteObject(ObservableSet<ModuleMember> selected) {
        ToolButton button = new ToolButtonImpl(I18n.$("tool-delete-nodeflow"), Icon.icon("delete-object"), mouseButton -> {
            selected.forEach(ModuleMember::remove);
            selected.clear();
        });
        button.impl_disableProperty().bind(Bindings.isEmpty(selected));
        return button;
    }

    public static ToolButton cut(ObservableSet<ModuleMember> selected) {
        ToolButton button = new ToolButtonImpl(I18n.$("tool-cut"), Icon.icon("cut"), mouseButton -> {
            ModuleMemberClipboard.putIntoClipboard(selected);
            selected.forEach(ModuleMember::remove);
            selected.clear();
        });
        button.impl_disableProperty().bind(Bindings.isEmpty(selected));
        return button;
    }

    public static ToolButton copy(ObservableSet<ModuleMember> selected) {
        ToolButton button = new ToolButtonImpl(I18n.$("tool-copy"), Icon.icon("copy"), mouseButton -> {
            ModuleMemberClipboard.putIntoClipboard(selected);
        });
        button.impl_disableProperty().bind(Bindings.isEmpty(selected));
        return button;
    }

    public static ToolButton paste(ObservableSet<ModuleMember> selected, NodeModule module) {
        ToolButton button = new ToolButtonImpl(I18n.$("tool-paste"), Icon.icon("paste"), mouseButton -> {
            ModuleMemberClipboard.paste(null, selected, module);
        });
        button.impl_disableProperty().bind(ModuleMemberClipboard.HAS_DATA.not());
        return button;
    }

    public static ToolButton searchFile(BooleanBinding disableProperty) {
        ToolButton button = new ToolButtonImpl(I18n.$("tool-search"), Icon.icon("search-file-content"), mouseButton -> {

        });
        button.impl_disableProperty().bind(disableProperty);
        return button;
    }

    public static ToolButton search(BooleanBinding disableProperty) {
        ToolButton button = new ToolButtonImpl(I18n.$("tool-search"), Icon.icon("search-tool"), mouseButton -> {

        });
        button.impl_disableProperty().bind(disableProperty);
        return button;
    }

    public static ToolRadio snapToGrid(NodeFileSession editor) {
        ToolRadio button = new ToolRadioImpl(I18n.$("tool-snap-to-grid"), Icon.icon("snap"), true);
        editor.impl_getViewport().getCanvas().snapToGridProperty().bind(button.impl_selectedProperty());
        button.impl_selectedProperty().addListener((obs, old, val) -> {
            if (val) {
                NodeCanvas canvas = editor.impl_getViewport().getCanvas();
                for (Node node : canvas.getNodes()) {
                    node.setLayoutX(Math.floor(node.getLayoutX() / 20d) * 20d);
                    node.setLayoutY(Math.floor(node.getLayoutY() / 20d) * 20d);
                }
                for (NodeGroup group : canvas.getGroups()) {
                    group.setLayoutX(Math.floor(group.getLayoutX() / 20d) * 20d);
                    group.setLayoutY(Math.floor(group.getLayoutY() / 20d) * 20d);
                }
            }
        });
        return button;
    }

    public static ToolRadio selectMode(NodeFileSession editor) {
        ToolRadio button = new ToolRadioImpl(I18n.$("tool-select-mode"), Icon.icon("select-mode"), false);
        button.setGroup(RadioButtonGroup.SELECT_MODE);
        button.impl_selectedProperty().addListener((obs, old, val) -> {
            editor.impl_getViewport().getCanvas().getSelectionContainer().getMode().set(ToolMode.SELECT);
        });
        return button;
    }

    public static ToolRadio showAnimation(NodeFileSession editor) {
        ToolRadio button = new ToolRadioImpl(I18n.$("tool-show-animation"), Icon.icon("animation"), true);
        button.impl_selectedProperty().addListener((obs, old, val) -> {
            editor.alwaysAnimateProperty().set(val);
        });
        return button;
    }

    public static ToolRadio groupMode(NodeFileSession editor) {
        ToolRadio button = new ToolRadioImpl(I18n.$("tool-group-mode"), Icon.icon("group-mode"), false);
        button.setGroup(RadioButtonGroup.SELECT_MODE);
        button.impl_selectedProperty().addListener((obs, old, val) -> {
            editor.impl_getViewport().getCanvas().getSelectionContainer().getMode().set(ToolMode.GROUPING);
        });
        return button;
    }

    static ToolComponent[] requestComponents(EditorUI window) {
        ArrayList<ToolComponent> components = new ArrayList<>();
        components.add(new ToolButtonImpl(I18n.$("tool-create-new-file"), Icon.icon("create-new-file"), mouseButton -> {
            Dialogs.createNewFile(window, window.getWindow().getProject(), null);
        }));
        components.add(deleteFileButton(window));
        components.add(new ToolSeparatorImpl());
        components.add(new ToolButtonImpl(I18n.$("tool-import-file"), Icon.icon("import-file"), mouseButton -> {
            List<File> files = Dialogs.importFiles(window.getWindow().getProject().getFacet(), window.getWindow(), I18n.$("import-file-to-project"));
            if (files != null) {
                for (File file : files) {
                    file.renameTo(new File(window.getWindow().getProject().getSourceDirectory().getPath(), file.getName()));
                }
            }
        }));
        components.add(new ToolButtonImpl(I18n.$("tool-export"), Icon.icon("export"), mouseButton -> {
            File target = Dialogs.exportProject(window.getWindow().getProject().getFacet(), window.getWindow(), I18n.$("export-project-title"));
            if (target != null) {
                Resource resource = ResourceManagerImpl.fileToResource(target);
                if (resource instanceof UnknownResource) {
                    resource = ((UnknownResource) resource).createFile();
                }
                if (resource instanceof WritableResourceFile) {
                    ProjectCompiler compiler = new ProjectCompiler(window.getWindow().getProject(), (WritableResourceFile) resource);
                    window.setOverlay(new OverlayUI(I18n.$("compiling")));
                    compiler.exportProject().andThen(result -> {
                        window.setOverlay(null);
                        if (result) {
                            NotificationPopup.showNotification("## Compilation\nProject successfully exported to "+target.getAbsolutePath());
                        }
                    }).andThenError(error -> {
                        System.out.println(error);
                        window.setOverlay(null);
                        NotificationPopup.showNotification("## Compilation\nFailed to export project:\n"+ error);
                        error.printStackTrace();
                    });
                }
            }
        }));
        components.add(new ToolSeparatorImpl());
        if (window.getWindow().getProject().getFacet().hasDebugSupport()) {
            components.add(new ToolButtonImpl(I18n.$("run-debug"), Icon.icon("run"), mouseButton -> {
                window.getWindow().getProject().runDebug().andThenError(error -> {
                    error.printStackTrace();
                });
            }));
        }
        components.add(new ToolButtonImpl(I18n.$("debug"), Icon.icon("debug"), mouseButton -> {
            MarkErrorDialogContent content = new MarkErrorDialogContent();
            thito.nodeflow.api.ui.dialog.Dialog dialog = Dialog.createDialog(content, 0, DialogButton.createTextButton(1, 0, I18n.$("button-cancel"), null, mouse -> {
                mouse.close();
            }), DialogButton.createTextButton(0, 0, I18n.$("button-ok"), null, mouseEvent -> {
                mouseEvent.close();
                Task.runOnBackground("inspect-files", () -> {
                    List<ExceptionParser.ParsedException> exceptions = ExceptionParser.parseExceptions(content.textProperty().get());
                    Map<String, HeadlessNodeModule> cached = new HashMap<>();
                    for (ExceptionParser.ParsedException e : exceptions) {
                        Resource resource = window.getWindow().getProject().getSourceDirectory().getChild(e.getFileName());
                        if (resource instanceof ResourceFile) {
                            HeadlessNodeModule module = cached.get(e.getFileName());
                            if (module == null) {
                                module = new HeadlessNodeModule(0, resource.getName(), (ResourceFile) resource);
                                try (InputStream inputStream = ((ResourceFile) resource).openInput()) {
                                    ModuleManagerImpl.getInstance().loadHeadlessModule(module, inputStream);
                                    cached.put(e.getFileName(), module);
                                } catch (Throwable t) {
                                    continue;
                                }
                            }
                            boolean found = false;
                            for (thito.nodeflow.api.editor.node.Node x : module.nodes()) {
                                if (x.getState().getID().equals(e.getID())) {
                                    found = true;
                                }
                            }
                            if (found) {
                                Task.runOnForeground("inspect-module", () -> {
                                    ProjectTabImpl tab = (ProjectTabImpl) window.getWindow().openFile((ResourceFile) resource);
                                    InvalidationListener update = obs -> {
                                        FileSession session = tab.sessionProperty().get();
                                        //!!NodeException: d4c2dc06-3739-49af-abfa-c31860b83580 (XX.ndx)
                                        if (session instanceof NodeFileSession) {
                                            new ArrayList<>(((NodeFileSession) session).getModule().nodes())
                                                    .forEach(node -> {
                                                        if (node.getState().getID().equals(e.getID())) {
                                                            node.getState().getTag().set(NodeTag.ERROR);
                                                            Toolkit.info("Marked "+node.getState().getID()+" as ERROR");
                                                        }
                                                    });

                                        }
                                    };
                                    tab.sessionProperty().addListener(update);
                                    update.invalidated(null);
                                });
                            }
                        }
                    }
                });
            }));
            dialog.open(window.getWindow());
        }));
        return components.toArray(new ToolComponent[0]);
    }
    public DefaultToolbar(EditorUI window) {
        super(null,
                requestComponents(window)
            );
    }
}
