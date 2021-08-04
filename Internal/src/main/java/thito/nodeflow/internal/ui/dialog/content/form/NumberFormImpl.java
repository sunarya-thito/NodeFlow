package thito.nodeflow.internal.ui.dialog.content.form;

import com.jfoenix.controls.*;
import com.jfoenix.validation.*;
import javafx.beans.property.*;
import javafx.scene.*;
import thito.nodeflow.api.locale.*;
import thito.nodeflow.api.ui.dialog.content.*;

public class NumberFormImpl extends AbstractFormImpl<Number> implements FormContent.NumberForm {
    private boolean allowDecimals; // if false, that means it only accepts integer and/or long

    public NumberFormImpl(I18nItem question, Number initialValue, boolean optional) {
        super(question, initialValue, optional);
    }

    private BooleanProperty disable = new SimpleBooleanProperty();
    @Override
    public BooleanProperty impl_disableProperty() {
        return disable;
    }

    @Override
    public void setAllowDecimals(boolean rounded) {
        allowDecimals = rounded;
    }

    @Override
    public boolean isAllowDecimals() {
        return allowDecimals;
    }

    @Override
    public Node createFieldPeer() {
        JFXTextField textField = new JFXTextField();
        textField.disableProperty().bind(disable);
        if (allowDecimals) {
            textField.setValidators(new DoubleValidator());
        } else {
            textField.setValidators(new IntegerValidator());
        }
        textField.textProperty().addListener((obs, old, val) -> {
            if (textField.validate()) {
                if (allowDecimals) {
                    answer(Double.valueOf(val));
                } else {
                    answer(Long.valueOf(val));
                }
            }
        });
        return textField;
    }
}
