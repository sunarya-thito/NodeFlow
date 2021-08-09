package thito.nodeflow.internal.ui.dialog.button;

import com.jfoenix.controls.*;
import thito.nodeflow.api.ui.dialog.button.*;
import thito.nodeflow.internal.*;

public class CheckBoxButtonPeerImpl extends JFXCheckBox implements CheckBoxButtonPeer {

    private CheckBoxDialogButton button;

    public CheckBoxButtonPeerImpl(CheckBoxDialogButton button) {
        this.button = button;
        Toolkit.style(this, "dialog-checkbox");
        textProperty().bind(button.getLabel().stringBinding());
        selectedProperty().bindBidirectional(button.impl_checkedProperty());
        disableProperty().bind(button.impl_disableProperty());
    }

    @Override
    public CheckBoxDialogButton getButton() {
        return button;
    }

    @Override
    public boolean isChecked() {
        return isSelected();
    }

    @Override
    public void setChecked(boolean checked) {
        setSelected(checked);
    }
}
