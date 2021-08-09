package thito.nodeflow.internal.ui.dialog.content.form;

import javafx.scene.*;
import javafx.scene.control.*;
import thito.nodeflow.api.locale.*;
import thito.nodeflow.api.ui.dialog.content.*;

public class RegexValidatorImpl implements FormContent.Validator {

    private String regex;
    private I18nItem message;
    public RegexValidatorImpl(String regex, I18nItem message) {
        this.regex = regex;
        this.message = message;
    }

    @Override
    public boolean validate(Node node) {
        if (node instanceof TextInputControl) {
            return ((TextInputControl) node).getText().matches(regex);
        }
        return false;
    }

    @Override
    public I18nItem getMessage() {
        return message;
    }
}
