package thito.nodeflow.internal.ui;

import javafx.beans.property.*;
import javafx.scene.control.*;
import thito.nodeflow.api.locale.*;
import thito.nodeflow.library.ui.layout.*;

public class OverlayUI extends UIComponent {

    @Component("text")
    private ObjectProperty<Label> text = new SimpleObjectProperty<>();
    private I18nItem item;
    public OverlayUI(I18nItem item) {
        this.item = item;
        setLayout(Layout.loadLayout("OverlayUI"));
    }

    public StringProperty textProperty() {
        return text.get().textProperty();
    }

    @Override
    protected void onLayoutReady() {
        textProperty().bind(item.stringBinding());
    }
}
