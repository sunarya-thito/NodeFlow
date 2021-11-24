package thito.nodeflow.ui.dialog;

import javafx.beans.value.ObservableValue;
import thito.nodeflow.language.I18n;

public class DialogButton {
    public static DialogButton create(I18n label) {
        return new DialogButton(label);
    }

    protected I18n label;
    protected Runnable exec;
    protected boolean defaultButton, mnemonics, cancelButton;
    protected ObservableValue<Boolean> disable;

    public DialogButton(I18n label) {
        this.label = label;
    }

    public DialogButton bindDisabled(ObservableValue<Boolean> disable) {
        this.disable = disable;
        return this;
    }

    public DialogButton cancelButton() {
        cancelButton = true;
        return this;
    }

    public DialogButton defaultButton() {
        defaultButton = true;
        return this;
    }

    public DialogButton mnemonics() {
        mnemonics = true;
        return this;
    }

    public DialogButton execution(Runnable r) {
        exec = r;
        return this;
    }

}
