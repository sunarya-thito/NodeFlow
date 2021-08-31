package thito.nodeflow.internal.ui.form;

import lombok.*;
import lombok.experimental.*;
import thito.nodeflow.library.language.*;
import thito.nodeflow.library.ui.form.*;
import thito.nodeflow.library.ui.form.node.*;

@FieldDefaults(level = AccessLevel.PUBLIC, makeFinal = true)
public class CreateProjectForm implements Form {
    FormProperty<String> name = new FormProperty<>(I18n.$("dashboard.forms.name"), new StringFormNode());
    FormProperty<String> description = new FormProperty<>(I18n.$("dashboard.forms.description"), new MultilineStringFormNode());
}
