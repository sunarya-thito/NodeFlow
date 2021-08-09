package thito.nodeflow.internal.editor.record;

import javafx.beans.*;
import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import thito.nodeflow.api.*;
import thito.nodeflow.api.locale.*;
import thito.nodeflow.api.ui.*;
import thito.nodeflow.api.ui.dialog.Dialog;
import thito.nodeflow.api.ui.dialog.button.*;
import thito.nodeflow.api.ui.dialog.content.*;
import thito.nodeflow.internal.Toolkit;
import thito.nodeflow.internal.ui.dialog.content.form.*;
import thito.nodeflow.library.binding.*;
import thito.nodeflow.library.ui.*;
import thito.nodeflow.library.ui.layout.*;

import java.util.*;

public class RecordFileUI extends UIComponent {
    @Component("list")
    private ObjectProperty<VBox> list = new SimpleObjectProperty<>();

    @Component("add-button")
    private ObjectProperty<BorderPane> add = new SimpleObjectProperty<>();

    private RecordFileSession session;

    public RecordFileUI(RecordFileSession session) {
        this.session = session;
        setLayout(Layout.loadLayout("RecordFileUI"));
    }

    public RecordFileSession getSession() {
        return session;
    }

    @Override
    protected void onLayoutReady() {
        list.get().getChildren().addListener((InvalidationListener) observable -> {
            boolean odd = true;
            for (Node child : list.get().getChildren()) {
                child.pseudoClassStateChanged(Pseudos.ODD, odd);
                odd = !odd;
            }
        });
        MappedListBinding.bind(list.get().getChildren(), session.getModule().getItems(), x -> new RecordFileItemUI(this, x));
        add.get().setOnMouseClicked(event -> {
            FormContent.StringForm nameForm = FormContent.StringForm.create(I18n.$("variable-name"), null, false);
            nameForm.addValidator(FormContent.Validator.regex(".+", I18n.$("variable-name-not-empty")));
            nameForm.addValidator(new FormContent.Validator() {
                @Override
                public boolean validate(Node node) {
                    if (node instanceof TextInputControl) {
                        for (RecordItem value : session.getModule().getItems()) {
                            if (((TextInputControl) node).getText().equals(value.getName())) {
                                return false;
                            }
                        }
                    }
                    return true;
                }

                @Override
                public I18nItem getMessage() {
                    return I18n.$("variable-already-exist");
                }
            });
            FormContent.ChoiceForm<String> typeForm = FormContent.ChoiceForm.create(I18n.$("variable-type"),
                    null,
                    new ArrayList<>(RecordFileManager.getManager().fetchTypes()), false);
            IntegerNumberFormImpl dimensions = new IntegerNumberFormImpl(I18n.$("variable-dimensions"), 0, true);
            FormContent content = FormContent.createContent(I18n.$("add-variable-title"), Pos.LEFT, new FormContent.Form[] {
                    nameForm, typeForm, dimensions
            });
            TextDialogButton button = DialogButton.createTextButton(0, 0, I18n.$("button-ok"), null, click -> {
                String type;
                if (dimensions.getAnswer().intValue() > 0) {
                    char[] x = new char[dimensions.getAnswer().intValue() + 1];
                    Arrays.fill(x, '[');
                    x[x.length - 1] = 'L';
                    type = new String(x) + typeForm.getAnswer() + ";";
                } else {
                    type = typeForm.getAnswer();
                }
                Class<?> c = NodeFlow.getApplication().getBundleManager().findClass(type);
                if (c != null) {
                    RecordItem item = new RecordItem(session.getModule(), UUID.randomUUID(), nameForm.getAnswer(), c);
                    session.getModule().getItems().add(item);
                }
                click.close();
            });
            button.impl_disableProperty().bind(Bindings.createBooleanBinding(() ->
                    !(typeForm.impl_answerProperty().get() != null
                            && nameForm.impl_answerProperty().get() != null), typeForm.impl_answerProperty(), nameForm.impl_answerProperty()));
            TextDialogButton button2 = DialogButton.createTextButton(0, 0, I18n.$("button-cancel"), null, click -> {
                click.close();
            });
            thito.nodeflow.api.ui.dialog.Dialog dialog = Dialog.createDialog(content, 0, button2, button);
            dialog.open(Toolkit.getWindow(this));
        });
    }
}
