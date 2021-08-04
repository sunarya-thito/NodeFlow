package thito.nodeflow.internal.ui.dialog.content;

import javafx.beans.property.*;
import javafx.scene.layout.*;
import thito.nodeflow.api.ui.dialog.*;
import thito.nodeflow.api.ui.dialog.content.*;
import thito.nodeflow.internal.ui.*;

import java.util.*;

public class ClassSelectContent implements DialogContent {

    private List<Class<?>> classList;
    private ObjectProperty<Class<?>> selected = new SimpleObjectProperty<>();

    public ClassSelectContent(List<Class<?>> classList) {
        this.classList = classList;
    }

    public ObjectProperty<Class<?>> selectedProperty() {
        return selected;
    }

    public Class<?> getSelected() {
        return selected.get();
    }

    @Override
    public Pane impl_createPeer(OpenedDialog dialog) {
        return new ClassSelectUI(classList, selected);
    }

}
