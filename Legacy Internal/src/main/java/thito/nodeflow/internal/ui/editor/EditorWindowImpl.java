package thito.nodeflow.internal.ui.editor;

import javafx.collections.*;
import javafx.embed.swing.*;
import javafx.scene.*;
import javafx.scene.image.*;
import thito.nodeflow.api.*;
import thito.nodeflow.api.editor.*;
import thito.nodeflow.api.locale.*;
import thito.nodeflow.api.project.*;
import thito.nodeflow.api.project.property.*;
import thito.nodeflow.api.resource.*;
import thito.nodeflow.api.task.*;
import thito.nodeflow.api.ui.*;
import thito.nodeflow.api.ui.menu.*;
import thito.nodeflow.internal.Toolkit;
import thito.nodeflow.internal.*;
import thito.nodeflow.internal.project.*;
import thito.nodeflow.internal.ui.*;
import thito.nodeflow.library.binding.*;

import javax.imageio.*;
import java.io.*;
import java.util.*;

public class EditorWindowImpl extends WindowImpl implements EditorWindow, ConfirmationClose {

    private Project project;
    private List<ProjectTab> tabs = new ArrayList<>();
    private ObservableList<ComponentProperty<?>> properties = FXCollections.observableArrayList();

    public EditorWindowImpl(Project project) {
        this.project = project;
        getStage().setMinWidth(800);
        getStage().setMinHeight(600);
        if (project != null) {
            impl_getPeer().titleProperty().bind(I18n.$("project-editor-title").stringBinding(project.getProperties().getName()));
        } else {
            impl_getPeer().setTitle("Untitled Project");
        }

        Menu menu = getMenu();
//        menu.getItems().add(requestDefaultFileMenu());
//        menu.getItems().add(requestDefaultEditMenu());
        menu.getItems().add(requestDefaultApplicationMenu());
        menu.getItems().add(requestDefaultWindowMenu());
        menu.getItems().add(requestDefaultHelpMenu());
    }

    @Override
    public void forceClose() {
        WritableImage image = ui == null ? null : ui.snapshot(new SnapshotParameters(), null);
        Task.runOnBackground("unload-project", () -> {
            if (image != null) {
                try {
                    try (OutputStream outputStream = ((WritableResourceFile) project.getProperties().getDirectory().getOrCreateChildFile("thumbnail.png")).openOutput()) {
                        ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", outputStream);
                    }
                } catch (Throwable t) {
                    // ignored
                    Toolkit.info("Failed to save project thumbnail");
                    t.printStackTrace();
                }
            }
            NodeFlow.getApplication().getProjectManager().unloadProject(project);
        });
        super.forceClose();
    }

    @Override
    public ObservableList<ComponentProperty<?>> getProperties() {
        return properties;
    }

    @Override
    public boolean askFirstBeforeClosing() {
        return false;
    }
//
//    protected MenuItem requestDefaultEditMenu() {
//        MenuItem item = MenuItem.create(I18n.$("menu-bar-edit"));
//        item.getChildren().addAll(
//                Arrays.asList(
//                        MenuItem.create(I18n.$("menu-bar-edit-undo")),
//                        MenuItem.create(I18n.$("menu-bar-edit-redo")),
//                        MenuItem.createSeparator(),
//                        MenuItem.create(I18n.$("menu-bar-edit-cut")),
//                        MenuItem.create(I18n.$("menu-bar-edit-copy")),
//                        MenuItem.create(I18n.$("menu-bar-edit-paste")),
//                        MenuItem.createSeparator(),
//                        MenuItem.create(I18n.$("menu-bar-edit-find"), MenuItemType.BUTTON_TYPE,
//                                    MenuItem.create(I18n.$("menu-bar-edit-find")),
//                                    MenuItem.create(I18n.$("menu-bar-edit-find-usages")),
//                                    MenuItem.create(I18n.$("menu-bar-edit-replace"))
//                                )
//                )
//        );
//        return item;
//    }
//
//    protected MenuItem requestDefaultFileMenu() {
//        MenuItem item = MenuItem.create(I18n.$("menu-bar-file"));
//        item.getChildren().addAll(
//                Arrays.asList(
//                        MenuItem.create(I18n.$("menu-bar-file-new"), () -> {
//                            if (ui != null) {
//                                Dialogs.createNewFile(ui, project);
//                            }
//                        }, MenuItemType.BUTTON_TYPE),
//                        MenuItem.create(I18n.$("menu-bar-file-open")),
//                        MenuItem.create(I18n.$("menu-bar-file-recent")),
//                        MenuItem.create(I18n.$("menu-bar-file-close")),
//                        MenuItem.createSeparator(),
//                        MenuItem.create(I18n.$("menu-bar-file-import")),
//                        MenuItem.create(I18n.$("menu-bar-file-export")),
//                        MenuItem.createSeparator(),
//                        MenuItem.create(I18n.$("menu-bar-file-save")),
//                        MenuItem.create(I18n.$("menu-bar-file-save-all")),
//                        MenuItem.create(I18n.$("menu-bar-file-reload"))
//                )
//        );
//        return item;
//    }

    @Override
    public String getName() {
        return "Editor";
    }

    private EditorUI ui;
    @Override
    protected void initializeViewport() {
        setViewport(ui = new EditorUI(this));
        MappedListBinding.bind(ui.getPropertyList().getChildren(), getProperties(), x -> (Node) x.impl_getPeer());
    }

    @Override
    public Project getProject() {
        return project;
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public ProjectTab openFile(ResourceFile file) {
        for (int i = 0; i < tabs.size(); i++) {
            if (tabs.get(i).getFile().equals(file)) {
                ProjectTab tab = tabs.get(i);
                tab.focus();
                return tab;
            }
        }
        FileHandler editor = NodeFlow.getApplication().getEditorManager().getRegisteredHandler(file.getExtension());
        ProjectTab tab = new ProjectTabImpl(this,
                file,
                editor
        );

        tabs.add(tab);
        ui.addTab(tab);
        return tab;
    }

    public List<ProjectTab> getTabs() {
        return tabs;
    }

    @Override
    public List<ProjectTab> getOpenedFiles() {
        return Collections.unmodifiableList(tabs);
    }

    @Override
    public void closeFile(ProjectTab tab) {
        ui.removeTab(tab);
        tabs.remove(tab);
        tab.closeFile();
    }
}
