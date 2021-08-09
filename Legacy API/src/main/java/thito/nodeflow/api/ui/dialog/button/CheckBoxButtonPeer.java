package thito.nodeflow.api.ui.dialog.button;

public interface CheckBoxButtonPeer extends ButtonPeer {
    boolean isChecked();

    void setChecked(boolean checked);

    @Override
    CheckBoxDialogButton getButton();
}
