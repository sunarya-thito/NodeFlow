package thito.nodeflow.internal.ui;

import javafx.collections.*;
import thito.nodeflow.api.ui.*;

import java.util.*;

public class RadioButtonGroupImpl implements RadioButtonGroup {
    private String name;
    private int amount;

    private ObservableList<RadioButton> selected = FXCollections.observableArrayList();

    public RadioButtonGroupImpl(String name, int amount) {
        this.name = name;
        this.amount = amount;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getMaximumAmount() {
        return amount;
    }

    @Override
    public List<RadioButton> getSelected() {
        return selected;
    }

    @Override
    public void attemptSelect(RadioButton button) {
        selected.add(button);
        if (selected.size() >= getMaximumAmount()) {
            int unSelectAmount = selected.size() + 1 - getMaximumAmount();
            for (int i = selected.size() - 1; i >= 0; i--) {
                RadioButton bx = selected.get(i);
                if (bx != button) {
                    if (unSelectAmount > 0) {
                        bx.setSelected(false);
                        unSelectAmount--;
                    } else break;
                }
            }
        }
    }

    @Override
    public void attemptUnSelect(RadioButton button) {
        selected.remove(button);
    }
}
