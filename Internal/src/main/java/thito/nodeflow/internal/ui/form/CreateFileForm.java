package thito.nodeflow.internal.ui.form;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import thito.nodeflow.internal.language.I18n;
import thito.nodeflow.internal.plugin.PluginManager;
import thito.nodeflow.internal.project.module.FileModule;
import thito.nodeflow.internal.ui.form.node.ListFormNode;
import thito.nodeflow.internal.ui.form.node.StringFormNode;

@FieldDefaults(level = AccessLevel.PUBLIC, makeFinal = true)
public class CreateFileForm implements Form {
    FormProperty<String> directory = new FormProperty<>(I18n.$("dialogs.new-file.fields.directory"), new StringFormNode());
    FormProperty<String> name = new FormProperty<>(I18n.$("dialogs.new-file.fields.name"), new StringFormNode());
    FormProperty<FileModule> type = new FormProperty<>(I18n.$("dialogs.new-file.fields.type"), new ListFormNode<>(PluginManager.getPluginManager().getModuleList(), module -> module == null ? null : module.getDisplayName().get()));
}
