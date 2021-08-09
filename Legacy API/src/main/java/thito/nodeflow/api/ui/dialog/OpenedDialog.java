package thito.nodeflow.api.ui.dialog;

import thito.nodeflow.api.task.FutureSupplier;
import thito.nodeflow.api.ui.Window;
import thito.nodeflow.api.ui.dialog.button.DialogButton;

public interface OpenedDialog {
    Dialog getDialog();

    Window getOwner();

    void close(DialogButton button);

    FutureSupplier<DialogButton> getActor();
}
