package thito.nodeflow.internal.node.property;

import javafx.beans.property.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import thito.nodeflow.library.ui.layout.*;

public class ComponentPropertyUI extends UIComponent {

    @Component("name")
    private final ObjectProperty<Label> name = new SimpleObjectProperty<>();

    @Component("peer")
    private final ObjectProperty<BorderPane> peer = new SimpleObjectProperty<>();

    private ComponentPropertyImpl property;

    public ComponentPropertyUI(ComponentPropertyImpl property) {
        this.property = property;
        setLayout(Layout.loadLayout("ComponentPropertyUI"));
    }

    @Override
    protected void onLayoutReady() {
        name.get().textProperty().bind(property.getName().stringBinding());
        peer.get().setCenter(property.getHandler().impl_getPeer());
    }
}
