package thito.nodeflow.api.ui.action;

import thito.nodeflow.api.ui.dialog.OpenedDialog;
import thito.nodeflow.api.ui.dialog.button.DialogButton;

public class ClickAction {

    private final OpenedDialog dialog;
    private int clickCount;
    private MouseButton button;
    private DialogButton dialogButton;

    public ClickAction(int clickCount, MouseButton button, OpenedDialog dialog, DialogButton dialogButton) {
        this.clickCount = clickCount;
        this.button = button;
        this.dialog = dialog;
        this.dialogButton = dialogButton;
    }

    public DialogButton getDialogButton() {
        return dialogButton;
    }

    public void setDialogButton(DialogButton dialogButton) {
        this.dialogButton = dialogButton;
    }

    public OpenedDialog getDialog() {
        return dialog;
    }

    public void close() {
        dialog.close(dialogButton);
    }

    public int getClickCount() {
        return clickCount;
    }

    public void setClickCount(int clickCount) {
        this.clickCount = clickCount;
    }

    public MouseButton getButton() {
        return button;
    }

    public void setButton(MouseButton button) {
        this.button = button;
    }

    public enum MouseButton {
        LEFT, MIDDLE, RIGHT
    }
}
