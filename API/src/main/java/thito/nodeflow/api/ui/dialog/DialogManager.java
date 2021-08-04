package thito.nodeflow.api.ui.dialog;

import javafx.beans.value.*;
import thito.nodeflow.api.locale.I18nItem;
import thito.nodeflow.api.task.Task;
import thito.nodeflow.api.ui.*;
import thito.nodeflow.api.ui.action.ClickAction;
import thito.nodeflow.api.ui.dialog.button.CheckBoxDialogButton;
import thito.nodeflow.api.ui.dialog.button.DialogButton;
import thito.nodeflow.api.ui.dialog.button.TextDialogButton;
import thito.nodeflow.api.ui.dialog.content.*;

import java.util.*;
import java.util.function.Consumer;

public interface DialogManager {
    Dialog createDialog(DialogContent content, int options, DialogButton... buttons);

    TextDialogButton createTextButton(int id, int behaviour, I18nItem label, Icon icon, Consumer<ClickAction> actionConsumer);

    CheckBoxDialogButton createCheckBoxButton(int id, int behaviour, I18nItem label, Consumer<ClickAction> actionConsumer);

    MessageContent createMessageContent(Dialog.Type type, Dialog.Level level, I18nItem header, Pos headerAlignment, I18nItem message, Pos messageAlignment);

    ActionContent createActionContent(I18nItem header, Pos headerAlignment, ActionContent.Action... actions);

    ActionContent.Action createAction(I18nItem label, Image icon, Task task, boolean closeOnAction);

    FormContent createFormContent(I18nItem header, Pos headerAlignment, FormContent.Form<?>... forms);

    FormContent.StringForm createStringForm(I18nItem question, String initialValue, String validator, boolean optional);

    FormContent.NumberForm createNumberForm(I18nItem question, Number initialValue, boolean decimals, boolean optional);

    FormContent.BooleanForm createBooleanForm(I18nItem question, Boolean initialValue, boolean optional);

    <T> FormContent.ChoiceForm<T> createChoiceForm(I18nItem question, T initialValue, List<T> choices, boolean optional);

    <T> FormContent.ChoiceForm<T> createChoiceForm(I18nItem question, List<T> choices, boolean optional);

    <T> FormContent.Form<List<String[]>> createStringTableForm(I18nItem question, I18nItem[] columns, List<String[]> values, boolean optional);

    FormContent.StringListForm createStringListForm(I18nItem question, List<String> initialValues, boolean optional);

    FormContent.Validator createRegexValidator(String regex, I18nItem message);

    FormContent.Validator createPropertyValidator(ObservableValue<Boolean> value, I18nItem message);

    void createQuestionDialog(Window window, I18nItem title, I18nItem question, thito.nodeflow.api.ui.dialog.Dialog.Type type, thito.nodeflow.api.ui.dialog.Dialog.Level level, Consumer<Boolean> result);

    void createInfoDialog(Window window, I18nItem title, I18nItem question, thito.nodeflow.api.ui.dialog.Dialog.Type type, thito.nodeflow.api.ui.dialog.Dialog.Level level, Runnable result);

    default FormContent.StringForm createStringForm(I18nItem question, String initialValue, boolean optional) {
        return createStringForm(question, initialValue, null, optional);
    }
}
