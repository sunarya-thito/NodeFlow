package thito.nodeflow.internal.ui.dialog.content.form;

import com.jfoenix.controls.*;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.scene.*;
import thito.nodeflow.api.locale.*;
import thito.nodeflow.api.ui.dialog.content.*;
import thito.nodeflow.library.binding.*;

import java.util.*;

public class StringFormImpl extends AbstractFormImpl<String> implements FormContent.StringForm {
    private String validator;
    private boolean multiline;
    private ObservableList<FormContent.Validator> validators = FXCollections.observableArrayList();

    public StringFormImpl(I18nItem question, String initialValue, boolean optional) {
        super(question, initialValue, optional);
        if (!optional) {
            validators.add(new RegexValidatorImpl(".+", I18n.$("field-required")));
        }
    }

    private BooleanProperty disable = new SimpleBooleanProperty();
    @Override
    public BooleanProperty impl_disableProperty() {
        return disable;
    }

    @Override
    public boolean answer(String answer) {
        if (validator != null && !answer.matches(validator)) return false;
        return super.answer(answer);
    }

    @Override
    public void addValidator(FormContent.Validator... validators) {
        for (FormContent.Validator validator : validators) {
            this.validators.add(validator);
        }
    }

    @Override
    public List<FormContent.Validator> getValidators() {
        return validators;
    }

    @Override
    public boolean multiline() {
        return multiline;
    }

    @Override
    public void setMultiline(boolean multiline) {
        this.multiline = multiline;
    }

    @Override
    public void setRule(String regex) {
        validator = regex;
    }

    @Override
    public String getRule() {
        return validator;
    }

    private String validate(String old, String string) {
        if (!string.isEmpty() && validator != null && !string.matches(validator)) {
            string = old;
        }
        int sub = 0;
        for (; sub < string.length(); sub++) if (!Character.isWhitespace(string.charAt(sub))) break;
        string = string.substring(sub);
        return string;
    }

    @Override
    public Node createFieldPeer() {
        if (multiline) {
            JFXTextArea area = new JFXTextArea();
            area.disableProperty().bind(disable);
            area.textProperty().addListener(obs -> area.validate());
            AdvancedBindings.bindFilterBidirectional(impl_answerProperty(), area.textProperty(), (old, string) -> validate(old, string));
            return area;
        }
        JFXTextField textField = new JFXTextField();
        textField.disableProperty().bind(disable);
        AdvancedBindings.bindFilterBidirectional(impl_answerProperty(), textField.textProperty(), (old, string) -> validate(old, string));
        return textField;
    }
}
