package thito.nodeflow.ui.form.internal;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import thito.nodeflow.language.I18n;
import thito.nodeflow.project.ProjectManager;
import thito.nodeflow.project.module.FileModule;
import thito.nodeflow.ui.form.Form;
import thito.nodeflow.ui.form.FormProperty;
import thito.nodeflow.ui.form.node.ListFormNode;
import thito.nodeflow.ui.form.node.StringFormNode;

@FieldDefaults(level = AccessLevel.PUBLIC, makeFinal = true)
public class CreateFileForm implements Form {
    FormProperty<String> directory = new FormProperty<>(I18n.$("dialogs.new-file.fields.directory"), new StringFormNode());
    FormProperty<String> name = new FormProperty<>(I18n.$("dialogs.new-file.fields.name"), new StringFormNode());
    FormProperty<FileModule> type = new FormProperty<>(I18n.$("dialogs.new-file.fields.type"), new ListFormNode<>(ProjectManager.getInstance().getModuleList(), module -> module == null ? null : module.getDisplayName().get()));
}
