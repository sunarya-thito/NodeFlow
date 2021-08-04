package thito.nodeflow.internal.ui.dialog;

import javafx.beans.property.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import thito.nodeflow.api.ui.dialog.*;
import thito.nodeflow.api.ui.dialog.button.*;
import thito.nodeflow.library.ui.layout.*;

public class DialogPeer extends UIComponent {

    private OpenedDialog dialog;

    @Component("border")
    private final ObjectProperty<BorderPane> border = new SimpleObjectProperty<>();
    @Component("viewport")
    private final ObjectProperty<BorderPane> viewport = new SimpleObjectProperty<>();
    @Component("content")
    private final ObjectProperty<BorderPane> content = new SimpleObjectProperty<>();
    @Component("buttons")
    private final ObjectProperty<FlowPane> buttons = new SimpleObjectProperty<>();

    public DialogPeer(OpenedDialog openedDialog) {
        this.dialog = openedDialog;
        setLayout(Layout.loadLayout("DialogUI"));
    }

    public BorderPane getViewport() {
        return viewport.get();
    }

    public BorderPane getContent() {
        return content.get();
    }

    public FlowPane getButtons() {
        return buttons.get();
    }

    @Override
    protected void onLayoutReady() {
        for (DialogButton button : dialog.getDialog().getButtons()) {
            Node btn = (Node) button.createPeer();
            btn.setOnMouseClicked(event -> {
                ((OpenedDialogImpl) dialog).dispatchClick(button, event);
            });
            buttons.get().getChildren().add(btn);
        }
    }
}
