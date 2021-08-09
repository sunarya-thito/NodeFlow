package thito.nodeflow.internal.ui.editor;

import javafx.beans.property.*;
import javafx.collections.*;
import javafx.scene.control.ListCell;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.util.*;
import thito.nodeflow.api.*;
import thito.nodeflow.api.task.*;
import thito.nodeflow.api.ui.*;
import thito.nodeflow.api.ui.dialog.*;
import thito.nodeflow.api.ui.list.*;
import thito.nodeflow.internal.Toolkit;
import thito.nodeflow.internal.*;
import thito.nodeflow.internal.node.provider.*;
import thito.nodeflow.internal.ui.dialog.content.*;
import thito.nodeflow.internal.ui.list.*;
import thito.nodeflow.library.ui.*;
import thito.nodeflow.library.ui.layout.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.*;

public class EnumSelectUI extends UIComponent {

    @Component("search")
    private ObjectProperty<TextField> search = new SimpleObjectProperty<>();
    @Component("list")
    private ObjectProperty<ListView<Field>> list = new SimpleObjectProperty<>();
    private SimpleTaskQueue taskQueue = new SimpleTaskQueue(TaskThread.BACKGROUND);
    private IconedListHandler handler;
    private EnumSelectContent content;
    private OpenedDialog dialog;
    public EnumSelectUI(EnumSelectContent content, OpenedDialog dialog) {
        this.content = content;
        this.dialog = dialog;
        handler = ((IconedListImpl) NodeFlow.getApplication().getUIManager().getIconedList()).get(content.getJavaClass());
        setLayout(Layout.loadLayout("EnumSelectUI"));
    }

    @Override
    protected void onLayoutReady() {
        search.get().textProperty().addListener((obs, old, val) -> updateSearch(val));
        ObservableList<Field> values = FXCollections.observableArrayList(Arrays.stream(content.getJavaClass().getDeclaredFields()).filter(field -> field.getType().equals(content.getJavaClass())).collect(Collectors.toList()));
        values.add(0, null);
        list.get().setCellFactory(new Callback<ListView<Field>, ListCell<Field>>() {
            @Override
            public ListCell<Field> call(ListView<Field> param) {
                ListCell<Field> field = new ListCell<Field>() {

                    @Override
                    protected void updateItem(Field item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                            setText(JavaNodeProviderCategory.capitalizeCamelCase(item.getName()));
                            if (handler != null) {
                                IconedContent content = handler.getContent(item);
                                if (content != null) {
                                    BetterImageView view = new BetterImageView();
                                    Icon icon = content.getIcon();
                                    if (icon == null) {
                                        icon = NodeFlow.getApplication().getResourceManager().getIcon("missing-object");
                                    } else {
                                        new ImageViewTooltip(view);
                                    }
                                    view.imageProperty().bind(icon.impl_propertyPeer());
                                    setGraphic(view);
                                    return;
                                }
                            }
                            BetterImageView view = new BetterImageView();
                            new ImageViewTooltip(view);
                            view.imageProperty().bind(NodeFlow.getApplication().getResourceManager().getIcon("missing-object").impl_propertyPeer());
                            setGraphic(view);
                        } else {
                            if (!empty) {
                                setText("< NULL >");
                                BetterImageView view = new BetterImageView();
                                view.imageProperty().bind(NodeFlow.getApplication().getResourceManager().getIcon("missing-object").impl_propertyPeer());
                                setGraphic(view);
                            } else {
                                setText(null);
                                setGraphic(null);
                            }
                        }
                    }
                };
                field.setOnMousePressed(event -> {
                    if (event.getClickCount() >= 2 && event.getButton() == MouseButton.PRIMARY) {
                        Field selected = list.get().getSelectionModel().getSelectedItem();
                        content.getResultConsumer().accept(selected);
                        dialog.close(null);
                    }
                });
                return field;
            }
        });
        updateSearch(null);
    }

    public void updateSearch(String search) {
        taskQueue.putQuery(() -> {
            List<Field> characters;
            List<Field> values = new ArrayList<>(Arrays.stream(content.getJavaClass().getDeclaredFields()).filter(field -> field.getType().equals(content.getJavaClass())).collect(Collectors.toList()));
            values.add(0, null);
            if (search == null || search.isEmpty()) {
                characters = values;
            } else {
                characters = new ArrayList<>();
                for (Field field : values) {
                    if (field == null || Toolkit.calculateSearchScore(JavaNodeProviderCategory.capitalizeCamelCase(field.getName()), search) > 0) {
                        characters.add(field);
                    }
                }
            }
            characters.sort((o1, o2) -> {
                if (o1 == null) {
                    return -1;
                }
                if (o2 == null) {
                    return 1;
                }
                if (search == null || search.isEmpty()) {
                    return o1.getName().compareToIgnoreCase(o2.getName());
                }
                return Integer.compare(Toolkit.calculateSearchScore(JavaNodeProviderCategory.capitalizeCamelCase(o2.getName()), search)
                        , Toolkit.calculateSearchScore(JavaNodeProviderCategory.capitalizeCamelCase(o1.getName()), search));
            });
            Task.runOnForeground("sort-enums", () -> {
                this.list.get().getItems().setAll(characters);
                this.list.get().scrollTo(0);
                taskQueue.markReady();
            });
        });
    }

}