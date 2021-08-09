package thito.nodeflow.internal.editor.config;

import javafx.beans.*;
import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.scene.*;
import javafx.scene.control.ListCell;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.*;
import thito.nodeflow.api.locale.*;
import thito.nodeflow.api.ui.*;
import thito.nodeflow.api.ui.dialog.Dialog;
import thito.nodeflow.api.ui.dialog.button.*;
import thito.nodeflow.api.ui.dialog.content.*;
import thito.nodeflow.internal.*;
import thito.nodeflow.internal.editor.config.savemodes.*;
import thito.nodeflow.internal.editor.config.type.*;
import thito.nodeflow.internal.ui.dialog.content.form.*;
import thito.nodeflow.library.binding.*;
import thito.nodeflow.library.ui.*;
import thito.nodeflow.library.ui.layout.*;

import java.util.*;

public class ConfigFileUI extends UIComponent {
    private ConfigFileModule module;

    @Component("list")
    private ObjectProperty<VBox> list = new SimpleObjectProperty<>();

    @Component("add-button")
    private ObjectProperty<BorderPane> add = new SimpleObjectProperty<>();

    @Component("mode")
    private ObjectProperty<ComboBox<ConfigFileSaveMode>> mode = new SimpleObjectProperty<>();

    @Component("allow-save")
    private ObjectProperty<CheckBox> save = new SimpleObjectProperty<>();

    @Component("allow-load")
    private ObjectProperty<CheckBox> load = new SimpleObjectProperty<>();

    public ConfigFileUI(ConfigFileModule module) {
        this.module = module;
        setLayout(Layout.loadLayout("ConfigFileUI"));
    }

    public VBox getList() {
        return list.get();
    }

    public ConfigFileModule getModule() {
        return module;
    }

    @Override
    protected void onLayoutReady() {
        mode.get().valueProperty().addListener((obs, old, val) -> {
            save.get().setDisable(val instanceof DisabledSaveMode);
            load.get().setDisable(val instanceof DisabledSaveMode);
            System.out.println("mode change to "+val.getId());
            module.setMode(val);
        });
        mode.get().setItems(FXCollections.observableArrayList(ConfigFileManager.getManager().getSaveModes()));
        mode.get().setValue(module.getMode());
        mode.get().setCellFactory(param -> new ListCell<ConfigFileSaveMode>() {
            @Override
            protected void updateItem(ConfigFileSaveMode item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    textProperty().unbind();
                    setText(null);
                } else {
                    textProperty().bind(item.getDisplayName().stringBinding());
                }
            }
        });
        save.get().setSelected(module.isAllowSave());
        load.get().setSelected(module.isAllowLoad());
        save.get().selectedProperty().addListener((obs, old, val) -> module.setAllowSave(val));
        load.get().selectedProperty().addListener((obs, old, val) -> module.setAllowLoad(val));
        mode.get().setButtonCell(new ListCell<ConfigFileSaveMode>() {
            @Override
            protected void updateItem(ConfigFileSaveMode item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    textProperty().unbind();
                    setText(null);
                } else {
                    textProperty().bind(item.getDisplayName().stringBinding());
                }
            }
        });
        list.get().getChildren().addListener((InvalidationListener) observable -> {
            boolean odd = true;
            for (Node child : list.get().getChildren()) {
                child.pseudoClassStateChanged(Pseudos.ODD, odd);
                odd = !odd;
            }
        });
        MappedListBinding.bind(list.get().getChildren(), module.getValues(), x -> new ConfigFileItemUI(this, x));
        add.get().setOnMouseClicked(event -> {
            FormContent.StringForm nameForm = FormContent.StringForm.create(I18n.$("variable-name"), null, false);
            nameForm.addValidator(FormContent.Validator.regex(".+", I18n.$("variable-name-not-empty")));
            nameForm.addValidator(new FormContent.Validator() {
                @Override
                public boolean validate(Node node) {
                    if (node instanceof TextInputControl) {
                        for (ConfigFileModule.Value value : module.getValues()) {
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
            FormContent.ChoiceForm<ConfigValueType> typeForm = FormContent.ChoiceForm.create(I18n.$("variable-type"),
                    null,
                    new ArrayList<>(ConfigFileManager.getManager().getValueTypes()), false);
            typeForm.setStringConverter(new StringConverter<ConfigValueType>() {

                @Override
                public String toString(ConfigValueType object) {
                    return object == null ? "?" : object.getDisplayName().getString();
                }

                @Override
                public ConfigValueType fromString(String string) {
                    return null;
                }

            });
            IntegerNumberFormImpl dimensions = new IntegerNumberFormImpl(I18n.$("variable-dimensions"), 0, true);
            dimensions.impl_disableProperty().bind(Bindings.createBooleanBinding(() ->
                !(typeForm.getAnswer() instanceof ObjectValueType)
            , typeForm.impl_answerProperty()));
            FormContent content = FormContent.createContent(I18n.$("add-variable-title"), Pos.LEFT, new FormContent.Form[] {
                    nameForm, typeForm, dimensions
            });
            TextDialogButton button = DialogButton.createTextButton(0, 0, I18n.$("button-ok"), null, click -> {
                String type;
                if (dimensions.getAnswer().intValue() > 0 && typeForm.getAnswer() instanceof ObjectValueType) {
                    char[] x = new char[dimensions.getAnswer().intValue() + 1];
                    Arrays.fill(x, '[');
                    x[x.length - 1] = 'L';
                    type = new String(x) + typeForm.getAnswer().getId() + ";";
                } else {
                    type = typeForm.getAnswer().getId();
                }
                ConfigFileModule.Value value = module.new Value(UUID.randomUUID(), type, nameForm.getAnswer(), null);
                module.getValues().add(value);
                click.close();
            });
            button.impl_disableProperty().bind(Bindings.createBooleanBinding(() ->
                    !(typeForm.impl_answerProperty().get() != null
                            && nameForm.impl_answerProperty().get() != null), typeForm.impl_answerProperty(), nameForm.impl_answerProperty()));
            TextDialogButton button2 = DialogButton.createTextButton(0, 0, I18n.$("button-cancel"), null, click -> {
                click.close();
            });
            Dialog dialog = Dialog.createDialog(content, 0, button2, button);
            dialog.open(Toolkit.getWindow(this));
        });
    }
}
