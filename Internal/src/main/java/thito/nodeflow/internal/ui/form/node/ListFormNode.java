package thito.nodeflow.internal.ui.form.node;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.util.StringConverter;
import thito.nodeflow.internal.binding.WeakReferencedListener;
import thito.nodeflow.internal.ui.form.FormNode;
import thito.nodeflow.internal.ui.form.FormProperty;

import java.util.List;
import java.util.function.Function;

public class ListFormNode<T> implements FormNode<T> {
    private ComboBox<T> comboBox = new ComboBox<>();

    public ListFormNode(List<T> list, Function<T, String> stringConverter) {
        this(list, new StringConverter<T>() {
            @Override
            public String toString(T object) {
                return stringConverter.apply(object);
            }

            @Override
            public T fromString(String string) {
                return null;
            }
        });
    }

    public ListFormNode(List<T> list, StringConverter<T> stringConverter) {
        comboBox.setConverter(stringConverter);
        if (list instanceof ObservableList) {
            comboBox.setItems((ObservableList<T>) list);
        } else {
            comboBox.setItems(FXCollections.observableList(list));
        }
    }
    @Override
    public Node getNode() {
        return comboBox;
    }

    private boolean update;
    @Override
    public void initialize(FormProperty<T> property) {
        property.addListener(new WeakReferencedListener(obs -> {
            if (update) return;
            update = true;
            comboBox.getSelectionModel().select(property.get());
            update = false;
        }, this));
        comboBox.getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> {
            if (update) return;
            update = true;
            property.set(val);
            update = false;
        });
    }
}
