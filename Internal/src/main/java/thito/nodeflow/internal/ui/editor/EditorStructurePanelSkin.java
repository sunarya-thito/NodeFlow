package thito.nodeflow.internal.ui.editor;

import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Callback;
import thito.nodeflow.internal.binding.MappedListBinding;
import thito.nodeflow.internal.project.module.FileStructure;
import thito.nodeflow.internal.ui.Component;
import thito.nodeflow.internal.ui.Skin;

public class EditorStructurePanelSkin extends Skin {
    private EditorSkin editorSkin;

    @Component("structure-tree")
    TreeView<FileStructure.Item> structure;

    public EditorStructurePanelSkin(EditorSkin editorSkin) {
        this.editorSkin = editorSkin;
    }

    @Override
    protected void onLayoutLoaded() {
//        structure.rootProperty().bind(MappedBinding.map(MappedBinding.flatMap(editorSkin.getFileTabs().getSelectionModel().selectedItemProperty(),
//                x -> {
//                    if (x == null) return null;
//                    FileTab fileTab = (FileTab) x.getProperties().get(FileTab.class);
//                    if (fileTab == null) return null;
//                    FileStructure structure = fileTab.getFileViewer().getStructure();
//                    if (structure == null) return null;
//                    return structure.rootProperty();
//                }), this::createItem));
        structure.addEventHandler(KeyEvent.KEY_TYPED, event -> {
            if (event.getCode() == KeyCode.DELETE) {

            }
            // TODO Delete confirmation
            // TODO Dispatch delete for all selected
        });
        structure.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        structure.setCellFactory(new Callback<>() {
            @Override
            public TreeCell<FileStructure.Item> call(TreeView<FileStructure.Item> param) {
                return new TreeCell<>() {
                    {
                        addEventHandler(KeyEvent.KEY_TYPED, event -> {
                            if (event.getCode() == KeyCode.F2) {
                                FileStructure.Item item = getItem();
                                if (item != null) {
                                    renameItem(item);
                                }
                            }
                        });
//                        addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
//                            if (event.getClickCount() == 2 && event.getButton() == MouseButton.PRIMARY) {
//                                FileStructure.Item item = getItem();
//                                if (item != null) {
//                                    item.dispatchFocus();
//                                }
//                            }
//                        });
                        setEditable(true);
                    }

                    @Override
                    public void updateItem(FileStructure.Item item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!empty) {
                            if (item != null) {
                                item.dispatchFocus();
                                setContextMenu(item.getContextMenu());
                                setText(item.nameProperty().get());
                                setGraphic(new ImageView(new Image(item.getIconURL())));
                            } else {
                                setContextMenu(null);
                                setGraphic(null);
                                textProperty().unbind();
                                setText(null);
                            }
                        } else {
                            setContextMenu(null);
                            setGraphic(null);
                            textProperty().unbind();
                            setText(null);
                        }
                    }
                };
            }
        });
    }

    protected void renameItem(FileStructure.Item item) {

    }
    protected TreeItem<FileStructure.Item> createItem(FileStructure.Item item) {
        if (item == null) return null;
        TreeItem<FileStructure.Item> x = new TreeItem<>(item);
        MappedListBinding.bind(x.getChildren(), item.getUnmodifiableChildren(), this::createItem);
        return x;
    }
}
