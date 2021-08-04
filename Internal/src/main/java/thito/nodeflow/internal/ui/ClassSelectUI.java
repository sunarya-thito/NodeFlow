package thito.nodeflow.internal.ui;

import javafx.beans.property.*;
import javafx.collections.*;
import javafx.scene.control.*;
import javafx.util.*;
import thito.nodeflow.api.task.*;
import thito.nodeflow.internal.*;
import thito.nodeflow.library.ui.layout.*;

import java.util.*;

public class ClassSelectUI extends UIComponent {

    @Component("search")
    private ObjectProperty<TextField> search = new SimpleObjectProperty<>();
    @Component("list")
    private ObjectProperty<ListView<Class<?>>> list = new SimpleObjectProperty<>();
    private SimpleTaskQueue taskQueue = new SimpleTaskQueue(TaskThread.BACKGROUND);

    private List<Class<?>> classes;
    private ObjectProperty<Class<?>> selected;
    public ClassSelectUI(List<Class<?>> classes, ObjectProperty<Class<?>> selected) {
        this.selected = selected;
        this.classes = classes;
        setLayout(Layout.loadLayout("ClassSelectUI"));
    }

    @Override
    protected void onLayoutReady() {
        list.get().setItems(FXCollections.observableList(classes));
        search.get().textProperty().addListener((obs, old, val) -> {
            taskQueue.putQuery(() -> {
                List<Class<?>> copied = new ArrayList<>(classes);
                copied.removeIf(x -> Toolkit.calculateSearchScore(x.getName(), val) <= 0);
                copied.sort((a, b) -> Toolkit.calculateSearchScore(b.getName(), val) - Toolkit.calculateSearchScore(b.getName(), val));
                Task.runOnForeground("refresh-list", () -> {
                    list.get().setItems(FXCollections.observableList(copied));
                });
                taskQueue.markReady();
            });
        });
        list.get().setCellFactory(new Callback<ListView<Class<?>>, ListCell<Class<?>>>() {
            @Override
            public ListCell<Class<?>> call(ListView<Class<?>> param) {
                ListCell<Class<?>> cell = new ListCell<Class<?>>() {
                    @Override
                    protected void updateItem(Class<?> item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!empty && item != null) {
                            setText(item.getName());
                        } else {
                            setText(null);
                        }
                    }
                };
                cell.setOnMouseClicked(event -> {
                    if (event.getClickCount() >= 2) {
                        selected.set(cell.getItem());
                    }
                });
                return cell;
            }
        });
    }
}
