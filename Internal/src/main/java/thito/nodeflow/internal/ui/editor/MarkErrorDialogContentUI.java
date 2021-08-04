package thito.nodeflow.internal.ui.editor;

import javafx.beans.property.*;
import javafx.scene.control.*;
import thito.nodeflow.library.ui.layout.*;

public class MarkErrorDialogContentUI extends UIComponent {
    @Component("text")
    private ObjectProperty<TextArea> text = new SimpleObjectProperty<>();
    private StringProperty property;
    public MarkErrorDialogContentUI(StringProperty property) {
        this.property = property;
        setLayout(Layout.loadLayout("MarkDebugUI"));
    }

    @Override
    protected void onLayoutReady() {
        text.get().textProperty().bindBidirectional(property);
    }
}
