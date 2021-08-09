package thito.nodeflow.internal.ui.dialog;

import javafx.beans.value.*;
import javafx.collections.*;
import thito.nodeflow.api.locale.*;
import thito.nodeflow.api.task.*;
import thito.nodeflow.api.ui.*;
import thito.nodeflow.api.ui.action.*;
import thito.nodeflow.api.ui.dialog.*;
import thito.nodeflow.api.ui.dialog.button.*;
import thito.nodeflow.api.ui.dialog.content.*;
import thito.nodeflow.internal.ui.dialog.button.*;
import thito.nodeflow.internal.ui.dialog.content.*;
import thito.nodeflow.internal.ui.dialog.content.form.*;

import java.util.*;
import java.util.function.*;

public class DialogManagerImpl implements DialogManager {

    @Override
    public Dialog createDialog(DialogContent content, int options, DialogButton... buttons) {
        return new DialogImpl(options, content, buttons);
    }

    @Override
    public <T> FormContent.Form<List<String[]>> createStringTableForm(I18nItem question, I18nItem[] columns, List<String[]> values, boolean optional) {
        return new PropertiesFormImpl(question, columns, values, optional);
    }

    @Override
    public TextDialogButton createTextButton(int id, int behaviour, I18nItem label, Icon icon, Consumer<ClickAction> actionConsumer) {
        return new TextDialogButtonImpl(id, behaviour, actionConsumer, label, icon);
    }

    @Override
    public CheckBoxDialogButton createCheckBoxButton(int id, int behaviour, I18nItem label, Consumer<ClickAction> actionConsumer) {
        return new CheckBoxDialogButtonImpl(id, behaviour, actionConsumer, label);
    }

    @Override
    public MessageContent createMessageContent(Dialog.Type type, Dialog.Level level, I18nItem header, Pos headerAlignment, I18nItem message, Pos messageAlignment) {
        return new MessageContentImpl(type, level, header, headerAlignment, message, messageAlignment);
    }

    @Override
    public ActionContent createActionContent(I18nItem header, Pos headerAlignment, ActionContent.Action... actions) {
        return new ActionContentImpl(header, headerAlignment, actions);
    }

    @Override
    public ActionContent.Action createAction(I18nItem label, Image icon, Task task, boolean closeOnAction) {
        return new ActionContentImpl.ActionImpl(label, icon, task, closeOnAction);
    }

    @Override
    public FormContent createFormContent(I18nItem header, Pos headerAlignment, FormContent.Form<?>... forms) {
        return new FormContentImpl(header, headerAlignment, forms);
    }

    @Override
    public FormContent.StringForm createStringForm(I18nItem question, String initialValue, String validator, boolean optional) {
        StringFormImpl form = new StringFormImpl(question, initialValue, optional);
        form.setRule(validator);
        return form;
    }

    @Override
    public FormContent.NumberForm createNumberForm(I18nItem question, Number initialValue, boolean decimals, boolean optional) {
        FormContent.NumberForm form = new NumberFormImpl(question, initialValue, optional);
        form.setAllowDecimals(decimals);
        return form;
    }

    @Override
    public FormContent.BooleanForm createBooleanForm(I18nItem question, Boolean initialValue, boolean optional) {
        return new BooleanFormImpl(question, initialValue, optional);
    }

    @Override
    public <T> FormContent.ChoiceForm<T> createChoiceForm(I18nItem question, T initialValue, List<T> choices, boolean optional) {
        return new ChoiceFormImpl<>(question, initialValue, choices, optional);
    }

    @Override
    public FormContent.StringListForm createStringListForm(I18nItem question, List<String> initialValues, boolean optional) {
        return new StringListFormImpl(question, FXCollections.observableArrayList(initialValues), optional);
    }

    @Override
    public <T> FormContent.ChoiceForm<T> createChoiceForm(I18nItem question, List<T> choices, boolean optional) {
        return new ChoiceFormImpl<>(question, choices, optional);
    }

    @Override
    public FormContent.Validator createRegexValidator(String regex, I18nItem message) {
        return new RegexValidatorImpl(regex, message);
    }

    @Override
    public FormContent.Validator createPropertyValidator(ObservableValue<Boolean> value, I18nItem message) {
        return new BooleanValidatorImpl(value, message);
    }

    @Override
    public void createQuestionDialog(Window window, I18nItem title, I18nItem question, Dialog.Type type, Dialog.Level level, Consumer<Boolean> result) {
        Dialogs.ask(window, title, question, type, level, result);
    }

    @Override
    public void createInfoDialog(Window window, I18nItem title, I18nItem question, Dialog.Type type, Dialog.Level level, Runnable result) {
        Dialogs.inform(window, title, question, type, level, result);
    }
}
