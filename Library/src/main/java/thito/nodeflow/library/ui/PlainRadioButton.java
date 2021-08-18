package thito.nodeflow.library.ui;

import javafx.scene.control.*;

public class PlainRadioButton extends ToggleButton {
    @Override
    public void fire() {
        if (getToggleGroup() == null || !isSelected()) {
            super.fire();
        }
    }
}
