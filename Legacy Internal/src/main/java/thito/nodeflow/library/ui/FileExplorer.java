package thito.nodeflow.library.ui;

import javafx.beans.Observable;
import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.embed.swing.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import thito.nodeflow.api.*;
import thito.nodeflow.api.editor.*;
import thito.nodeflow.api.locale.*;
import thito.nodeflow.api.resource.*;
import thito.nodeflow.api.task.*;
import thito.nodeflow.api.ui.dialog.Dialog;
import thito.nodeflow.internal.Toolkit;
import thito.nodeflow.internal.clipboard.*;
import thito.nodeflow.internal.resource.*;
import thito.nodeflow.internal.ui.dialog.*;
import thito.nodeflow.internal.ui.editor.*;
import thito.nodeflow.library.*;

import javax.swing.*;
import javax.swing.filechooser.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import java.util.function.*;

public class FileExplorer extends ModernScrollPane {
    private static Image icon(Resource resource) {
        if (resource instanceof ResourceDirectory) {
            return thito.nodeflow.api.ui.Icon.icon("folder").impl_propertyPeer().get();
        }
        if (resource instanceof ResourceFile) {
            FileHandler handler = NodeFlow.getApplication().getEditorManager().getRegisteredHandler(((ResourceFile) resource).getExtension());
            if (handler != null) {
                Image img = handler.getIcon().impl_propertyPeer().get();
                if (img != null) {
                    return img;
                }
            }
        }
        Icon icon = FileSystemView.getFileSystemView().getSystemIcon(new File(resource.getPath()));
        if (icon == null) return null;
        return SwingFXUtils.toFXImage((BufferedImage) ((ImageIcon) icon).getImage(), null);
    }

    public static final DataFormat CUT_STATE = new DataFormat("FileExplorer.cut");
    private ObjectProperty<ResourceDirectory> root = new SimpleObjectProperty<>();
    private TreeView<Resource> resourceTree;
    private ObservableSet<FileListener> listeners = FXCollections.observableSet();
    private EditorUI window;

    public FileExplorer() {
        resourceTree = new TreeView<>();
        resourceTree.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        resourceTree.setShowRoot(false);
        resourceTree.setCellFactory(param -> new FileCell());
        setContent(resourceTree);

        root.addListener(this::updateRoot);

        Menu newFile = new Menu();
        newFile.textProperty().bind(I18n.$("new").stringBinding());

        MenuItem folder = new MenuItem();
        folder.textProperty().bind(I18n.$("folder").stringBinding());
        ImageView folderIcon = new ImageView();
        folderIcon.imageProperty().bind(thito.nodeflow.api.ui.Icon.icon("folder").impl_propertyPeer());
        folder.setGraphic(folderIcon);
        newFile.getItems().addAll(folder, new SeparatorMenuItem());

        for (FileHandler handler : NodeFlow.getApplication().getEditorManager().getEditors()) {
            MenuItem item = new MenuItem(handler.getName());
            item.setOnAction(event -> {
                Dialogs.createNewFile(window, window.getWindow().getProject(), handler);
            });
            ImageView icon = new ImageView();
            icon.imageProperty().bind(handler.getIcon().impl_propertyPeer());
            item.setGraphic(icon);
            newFile.getItems().add(item);
        }

        SeparatorMenuItem separator3 = new SeparatorMenuItem();

        MenuItem cutFile = new MenuItem();
        cutFile.disableProperty().bind(Bindings.isEmpty(resourceTree.getSelectionModel().getSelectedItems()));
        cutFile.textProperty().bind(I18n.$("file-cut").stringBinding());
        cutFile.setOnAction(event -> {
            List<File> files = new ArrayList<>();
            for (TreeItem<Resource> selected : resourceTree.getSelectionModel().getSelectedItems()) {
                if (selected.getValue() != null) {
                    files.add(new File(selected.getValue().getPath()));
                }
            }
            Clipboard clipboard = Clipboard.getSystemClipboard();
            Map<DataFormat, Object> content = new HashMap<>();
            content.put(CUT_STATE, true);
            content.put(DataFormat.FILES, files);
            clipboard.setContent(content);
        });

        MenuItem copyFile = new MenuItem();
        copyFile.disableProperty().bind(Bindings.isEmpty(resourceTree.getSelectionModel().getSelectedItems()));
        copyFile.textProperty().bind(I18n.$("file-copy").stringBinding());
        copyFile.setOnAction(event -> {
            List<File> files = new ArrayList<>();
            for (TreeItem<Resource> selected : resourceTree.getSelectionModel().getSelectedItems()) {
                if (selected.getValue() != null) {
                    files.add(new File(selected.getValue().getPath()));
                }
            }
            Clipboard clipboard = Clipboard.getSystemClipboard();
            Map<DataFormat, Object> content = new HashMap<>();
            content.put(DataFormat.FILES, files);
            clipboard.setContent(content);
        });

        MenuItem pasteFile = new MenuItem();
        pasteFile.disableProperty().bind(ModuleMemberClipboard.HAS_FILES.not());
        pasteFile.textProperty().bind(I18n.$("file-paste").stringBinding());
        pasteFile.setOnAction(event -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            if (clipboard.hasFiles()) {
                ResourceDirectory root;
                TreeItem<Resource> selected = resourceTree.getSelectionModel().getSelectedItem();
                if (selected == null || selected.getValue() == null) {
                    root = this.root.get();
                } else {
                    Resource resource = selected.getValue();
                    if (resource instanceof ResourceDirectory) {
                        root = (ResourceDirectory) resource;
                    } else {
                        root = resource.getParentDirectory();
                    }
                }
                List<File> files = clipboard.getFiles();
                Object cut = clipboard.getContent(CUT_STATE);
                Task.runOnBackground("file-paste", () -> {
                    FileOperationChain operationChain = new FileOperationChain(Toolkit.getWindow(this));
                    if (cut instanceof Boolean && (Boolean) cut) {
                        for (File file : files) {
                            operationChain.moveFile(file, new File(root.getPath()));
                        }
                    } else {
                        for (File file : files) {
                            operationChain.copyFile(file, new File(root.getPath()));
                        }
                    }
                    operationChain.execute();
                });
            }
        });

        SeparatorMenuItem separator2 = new SeparatorMenuItem();

        MenuItem deleteFile = new MenuItem();
        deleteFile.disableProperty().bind(Bindings.isEmpty(resourceTree.getSelectionModel().getSelectedItems()));
        deleteFile.textProperty().bind(I18n.$("tool-delete-file").stringBinding());
        deleteFile.setOnAction(event -> {
            List<PhysicalResource> files = new ArrayList<>();
            for (TreeItem<Resource> resource : getResourceTree().getSelectionModel().getSelectedItems()) {
                if (resource.getValue() instanceof PhysicalResource) {
                    files.add((PhysicalResource) resource.getValue());
                }
            }
            Dialogs.ask(window.getWindow(), I18n.$("file-delete-title"), I18n.$("file-delete"), Dialog.Type.QUESTION, Dialog.Level.WARN, result -> {
                if (result) {
                    Task.runOnBackground("delete", () -> {
                        files.forEach(f -> {
                            f.moveToRecycleBin();
                        });
                    });
                }
            });
        });

        SeparatorMenuItem separator = new SeparatorMenuItem();

        MenuItem renameFile = new MenuItem();
        renameFile.disableProperty().bind(Bindings.size(resourceTree.getSelectionModel().getSelectedItems()).isNotEqualTo(1));
        renameFile.textProperty().bind(I18n.$("file-rename").stringBinding());

        renameFile.setOnAction(event -> {
            Dialogs.renameFile(window, this, resourceTree.getSelectionModel().getSelectedItem().getValue());
        });

        ContextMenu menu = new ContextMenu(newFile, separator3, cutFile, copyFile, pasteFile, separator2, deleteFile, separator, renameFile);

        resourceTree.setContextMenu(menu);
    }

    public void setWindow(EditorUI window) {
        this.window = window;
    }

    public TreeItem<Resource> find(Resource resource) {
        return bulk(resource, getResourceTree().getRoot());
    }

    private TreeItem<Resource> bulk(Resource resource, TreeItem<Resource> parent) {
        if (parent.getValue() != null && parent.getValue().getPath().equalsIgnoreCase(resource.getPath())) {
            return parent;
        }
        for (TreeItem<Resource> item : parent.getChildren()) {
            TreeItem<Resource> result = bulk(resource, item);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    public ObservableSet<FileListener> getListeners() {
        return listeners;
    }

    public TreeView<Resource> getResourceTree() {
        return resourceTree;
    }

    public void setRoot(ResourceDirectory root) {
        this.root.set(root);
    }

    public ResourceDirectory getRoot() {
        return root.get();
    }

    public ObjectProperty<ResourceDirectory> rootProperty() {
        return root;
    }

    private void updateRoot(Observable observable, ResourceDirectory old, ResourceDirectory value) {
        TreeItem<Resource> root = resourceTree.getRoot();
        if (root instanceof FileItem) {
            ((FileItem) root).onRemoved();
        }
        root = new FileItem(value);
        ((FileItem) root).onAdded();
        resourceTree.setRoot(root);
    }

    public class FileItem extends TreeItem<Resource> implements Consumer<ResourceWatcherEvent> {
        private ResourceWatcher watcher;
        public FileItem(Resource value) {
            super(value);
            getChildren().addListener((ListChangeListener<TreeItem<Resource>>) c -> {
                while (c.next()) {
                    for (TreeItem<Resource> resourceTreeItem : c.getAddedSubList()) {
                        if (resourceTreeItem instanceof FileItem) {
                            ((FileItem) resourceTreeItem).onAdded();
                        }
                    }
                    for (TreeItem<Resource> resourceTreeItem : c.getRemoved()) {
                        if (resourceTreeItem instanceof FileItem) {
                            ((FileItem) resourceTreeItem).onRemoved();
                        }
                    }
                }
            });
            if (value instanceof ResourceDirectory) {
                for (Resource child : ((ResourceDirectory) value).getChildren()) {
                    getChildren().add(new FileItem(child));
                }
            }
        }

        protected void onAdded() {
            if (getValue() instanceof ResourceDirectory) {
                watcher = NodeFlow.getApplication().getResourceManager().getWatcherService().createWatcher((ResourceDirectory) getValue());
                watcher.addListener(this);
            }
        }


        @Override
        public void accept(ResourceWatcherEvent resourceWatcherEvent) {
            Task.runOnForeground("resource-watcher", () -> {
                Resource resource = resourceWatcherEvent.getResource();
                switch (resourceWatcherEvent.getType()) {
                    case CREATE:
                        getChildren().add(new FileItem(resource));
                        break;
                    case DELETE:
                        getChildren().removeIf(item -> Objects.equals(item.getValue().getPath(), resource.getPath()));
                        break;
                    case MODIFY:
                        break;
                }
                getListeners().forEach(listener -> listener.onFileChange(resourceWatcherEvent.getType(), resource));
            });
        }

        protected void onRemoved() {
            if (watcher != null) {
                NodeFlow.getApplication().getResourceManager().getWatcherService().destroyWatcher(watcher);
            }
        }
    }

    public class FileCell extends TreeCell<Resource> {

        public FileCell() {
            super();
            addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                if (isEmpty()) {
                    getResourceTree().getSelectionModel().clearSelection();
                }
            });
            itemProperty().addListener((obs, old, val) -> {
                if (val == null) {
                    getResourceTree().getSelectionModel().clearSelection(getIndex());
                }
            });
            setOnDragDetected(event -> {
                if (isEmpty() || getItem() == null) return;
                Dragboard board = startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                content.putFiles(Arrays.asList(new File(getItem().getPath())));
                board.setContent(content);
                event.consume();
            });
            setOnDragOver(event -> {
                if (event.getGestureSource() != this && event.getDragboard().hasFiles()) {
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                event.consume();
            });
            setOnDragEntered(event -> {
                if (event.getGestureSource() != this && event.getDragboard().hasFiles()) {
                    pseudoClassStateChanged(Pseudos.SELECTED, true);
                }
            });
            setOnDragExited(event -> {
                if (event.getGestureSource() != this && event.getDragboard().hasFiles()) {
                    pseudoClassStateChanged(Pseudos.SELECTED, false);
                }
            });
            setOnDragDropped(event -> {
                Dragboard db = event.getDragboard();
                boolean success = false;
                if (db.hasFiles()) {
                    Resource resource = getItem();
                    if (resource == null) resource = getRoot();
                    if (resource instanceof ResourceFile) {
                        resource = resource.getParentDirectory();
                    }
                    if (resource instanceof ResourceDirectory) {
                        success = true;
                        for (File file : db.getFiles()) {
                            File target = new File(resource.getPath(), file.getName());
                            if (!file.renameTo(target)) {
                                listeners.forEach(listener -> {
                                    listener.onFileMoved(ResourceManagerImpl.fileToResource(file), ResourceManagerImpl.fileToResource(target));
                                });
                                success = false;
                            }
                        }
                    }
                }
                event.setDropCompleted(success);
                event.consume();
            });
        }

        @Override
        protected void updateItem(Resource item, boolean empty) {
            super.updateItem(item, empty);
            if (item == null) {
                setText(null);
                setGraphic(null);
            } else {
                setText(item.getName());
                setGraphic(new ImageView(icon(item)));
            }
        }
    }

    public interface FileListener {
        void onFileChange(ResourceWatcherEvent.Type type, Resource resource);
        void onFileMoved(Resource from, Resource to);
    }

}
