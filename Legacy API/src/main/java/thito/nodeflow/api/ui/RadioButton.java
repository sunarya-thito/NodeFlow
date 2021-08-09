package thito.nodeflow.api.ui;

import javafx.beans.property.BooleanProperty;

public interface RadioButton {
    boolean isSelected();
    void setSelected(boolean selected);
    BooleanProperty impl_selectedProperty();
    RadioButtonGroup getGroup();
    void setGroup(RadioButtonGroup group);
}
