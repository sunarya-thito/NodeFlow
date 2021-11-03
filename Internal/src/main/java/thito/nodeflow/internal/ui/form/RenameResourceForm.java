package thito.nodeflow.internal.ui.form;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import thito.nodeflow.internal.language.I18n;
import thito.nodeflow.internal.ui.form.node.StringFormNode;

@FieldDefaults(makeFinal = true, level = AccessLevel.PUBLIC)
public class RenameResourceForm implements Form {
    FormProperty<String> newName = new FormProperty<>(I18n.$("dialogs.rename-file.fields.name"), new StringFormNode());
}
