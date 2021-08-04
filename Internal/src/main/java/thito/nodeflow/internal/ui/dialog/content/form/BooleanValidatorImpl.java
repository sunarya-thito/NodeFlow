package thito.nodeflow.internal.ui.dialog.content.form;

import javafx.beans.value.*;
import javafx.scene.*;
import thito.nodeflow.api.locale.*;
import thito.nodeflow.api.ui.dialog.content.*;

public class BooleanValidatorImpl implements FormContent.Validator {
    private ObservableValue<Boolean> booleanObservableValue;
    private I18nItem message;

    public BooleanValidatorImpl(ObservableValue<Boolean> booleanObservableValue, I18nItem message) {
        this.booleanObservableValue = booleanObservableValue;
        this.message = message;

    }

    public ObservableValue<Boolean> booleanProperty() {
        return booleanObservableValue;
    }

    @Override
    public boolean validate(Node node) {
        return booleanObservableValue.getValue();
    }

    @Override
    public I18nItem getMessage() {
        return message;
    }
}
