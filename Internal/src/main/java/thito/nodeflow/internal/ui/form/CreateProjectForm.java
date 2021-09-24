package thito.nodeflow.internal.ui.form;

import lombok.*;
import lombok.experimental.*;
import thito.nodeflow.internal.*;
import thito.nodeflow.internal.project.*;
import thito.nodeflow.internal.language.*;
import thito.nodeflow.internal.resource.*;
import thito.nodeflow.internal.ui.form.node.*;

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
            Resource resource = workspace.getRoot().getChild(name);
            if (resource.exists()) {
                return I18n.$("dashboard.forms.validate-already-exist");
            }
            return null;
        });
    }
}
