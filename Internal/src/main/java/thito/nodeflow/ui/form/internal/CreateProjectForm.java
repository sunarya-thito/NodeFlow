package thito.nodeflow.ui.form.internal;

import lombok.*;
import lombok.experimental.*;
import thito.nodeflow.NodeFlow;
import thito.nodeflow.language.I18n;
import thito.nodeflow.project.Workspace;
import thito.nodeflow.resource.Resource;
import thito.nodeflow.task.TaskThread;
import thito.nodeflow.ui.form.Form;
import thito.nodeflow.ui.form.FormProperty;
import thito.nodeflow.ui.form.node.*;

import java.util.*;

@FieldDefaults(level = AccessLevel.PUBLIC, makeFinal = true)
public class CreateProjectForm implements Form {
    FormProperty<String> name = new FormProperty<>(I18n.$("dashboard.forms.name"), new StringFormNode());
    FormProperty<String> description = new FormProperty<>(I18n.$("dashboard.forms.description"), new MultilineStringFormNode());
    FormProperty<String> folderName = new FormProperty<>(I18n.$("dashboard.forms.folder-name"), new StringFormNode());

    public CreateProjectForm() {
        name.addListener((obs, old, val) -> {
            if (Objects.equals(folderName.get(), old)) {
                folderName.set(val);
            }
        });
        name.validate(name -> name == null || name.trim().isEmpty() ? I18n.$("forms.validate-not-empty") : null);
        folderName.validate(name -> {
            if (name == null || name.trim().isEmpty()) return I18n.$("forms.validate-not-empty");
            Workspace workspace = NodeFlow.getInstance().workspaceProperty().get();
            Resource resource = TaskThread.IO().process(() -> workspace.getRoot().getChild(name));
            if (TaskThread.IO().process(resource::exists)) {
                return I18n.$("dashboard.forms.validate-already-exist");
            }
            return null;
        });
    }
}
