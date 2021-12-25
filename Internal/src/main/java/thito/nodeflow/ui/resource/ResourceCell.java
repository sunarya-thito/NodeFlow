package thito.nodeflow.ui.resource;

import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.*;
import thito.nodeflow.plugin.PluginManager;
import thito.nodeflow.project.module.FileModule;
import thito.nodeflow.project.module.UnknownFileModule;
import thito.nodeflow.resource.Resource;
import thito.nodeflow.resource.ResourceType;
import thito.nodeflow.task.TaskThread;
import thito.nodeflow.task.batch.Batch;
import thito.nodeflow.task.batch.Progress;
import thito.nodeflow.task.batch.TaskQueue;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ResourceCell extends TreeCell<Resource> {
    private static final PseudoClass hover = PseudoClass.getPseudoClass("hover");
    private ResourceExplorerView view;
    public ResourceCell(ResourceExplorerView view) {
        this.view = view;
        addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            if (isEmpty()) {
                view.getSelectionModel().clearSelection();
            }
        });
        addEventHandler(MouseEvent.DRAG_DETECTED, event -> {
            ObservableList<TreeItem<Resource>> selectedItems = view.getSelectionModel().getSelectedItems();
            if (!selectedItems.isEmpty()) {
                Dragboard dragboard = startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                content.putFiles(selectedItems.stream().map(x -> x.getValue().toFile()).collect(Collectors.toList()));
                dragboard.setContent(content);
                dragboard.setDragView(snapshot(null, new WritableImage((int) getWidth(), (int) getHeight())));
                event.consume();
            }
        });
        addEventHandler(DragEvent.DRAG_OVER, event -> {
            if (event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.ANY);
            }
        });
        addEventHandler(DragEvent.DRAG_ENTERED, event -> {
            if (event.getDragboard().hasFiles()) {
                pseudoClassStateChanged(hover, true);
            }
        });
        addEventHandler(DragEvent.DRAG_EXITED, event -> {
            if (event.getDragboard().hasFiles()) {
                pseudoClassStateChanged(hover, false);
            }
        });
        addEventHandler(DragEvent.DRAG_DROPPED, event -> {
            if (event.getDragboard().hasFiles()) {
                List<File> files = event.getDragboard().getFiles();
                if (files != null) {
                    event.setDropCompleted(true);
                    TreeItem<Resource> treeItem = getTreeItem();
                    if (treeItem == null) {
                        treeItem = view.getRoot();
                    }
                    if (treeItem != null) {
                        TreeItem<Resource> finalTreeItem = treeItem;
                        TaskQueue taskQueue = view.getTaskQueue();
                        Batch.execute(TaskThread.IO(), progress -> {
                            for (File f : files) {
                                progress.append(attemptCopy(finalTreeItem, null, f, event.getTransferMode() == TransferMode.MOVE));
                            }
                        }).start(taskQueue);
                    }
                }
            }
        });
        // auto updated by the resource watcher, no need to remove cell from tree view
//        addEventHandler(DragEvent.DRAG_DONE, event -> {
//            if (event.isAccepted()) {
//
//            }
//        });
    }

    private Batch.Task attemptCopy(TreeItem<Resource> finalTreeItem, File root, File value, boolean move) {
        if (value.exists()) {
            if (value.isDirectory()) {
                return Batch.execute(TaskThread.IO(), progress -> {
                    progress.setStatus("Listing "+value.getName());
                    try (Stream<Path> stream = Files.list(value.toPath())) {
                        stream.forEach(path -> {
                            progress.append(attemptCopy(finalTreeItem, value, path.toAbsolutePath().toFile(), move));
                        });
                    } finally {
                        if (move) {
                            progress.append(TaskThread.IO(), progress1 -> value.delete());
                        }
                    }
                });
            } else if (value.isFile()) {
                return Batch.execute(TaskThread.IO(), pr -> {
                    pr.setStatus((move ? "Moving" : "Copying")+" " + value.getName());
                    long file = value.length();
                    long count = 0;
                    byte[] buffer = new byte[128];
                    int len;
                    File target = new File(finalTreeItem.getValue().toFile(), root == null || root.getParentFile() == null ? value.getName() : value.getAbsolutePath().replace(root.getParentFile().getAbsolutePath(), ""));
                    File parent = target.getParentFile();
                    if (parent != null) {
                        parent.mkdirs();
                    }
                    if (!value.exists()) return;
                    if (move) {
                        value.renameTo(target);
                        return;
                    }
                    try (InputStream inputStream = new FileInputStream(value); OutputStream outputStream = new FileOutputStream(target)) {
                        while ((len = inputStream.read(buffer, 0, buffer.length)) != -1) {
                            outputStream.write(buffer, 0, len);
                            count += len;
                            pr.setProgress((double) count / (double) file);
                        }
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                });
            }
        }
        return null;
    }

    public ResourceExplorerView getView() {
        return view;
    }

    @Override
    protected void updateItem(Resource resource, boolean b) {
        super.updateItem(resource, b);
        if (!b) {
            TaskThread.BG().schedule(() -> {
                FileModule module = PluginManager.getPluginManager().getModule(resource);
                TaskThread.UI().schedule(() -> {
                    ImageView node = new ImageView();
                    node.imageProperty().bind(module.iconProperty());
                    setGraphic(node);
                });
            });
            setText(resource.getFileName());
        } else {
            setGraphic(null);
            setText(null);
        }
    }
}
