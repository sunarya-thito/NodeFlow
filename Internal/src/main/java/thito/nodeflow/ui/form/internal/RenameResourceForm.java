package thito.nodeflow.ui.form.internal;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import thito.nodeflow.language.I18n;
import thito.nodeflow.ui.form.Form;
import thito.nodeflow.ui.form.FormProperty;
import thito.nodeflow.ui.form.node.StringFormNode;

@FieldDefaults(makeFinal = true, level = AccessLevel.PUBLIC)
public class RenameResourceForm implements Form {
    FormProperty<String> newName = new FormProperty<>(I18n.$("dialogs.rename-file.fields.name"), new StringFormNode());
}
