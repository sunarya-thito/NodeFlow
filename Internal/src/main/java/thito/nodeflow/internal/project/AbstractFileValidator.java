package thito.nodeflow.internal.project;

import thito.nodeflow.internal.language.*;
import thito.nodeflow.internal.resource.*;
import thito.nodeflow.internal.ui.form.*;

public class AbstractFileValidator implements Validator<Resource> {
    @Override
    public I18n validate(Resource value) {
        return value.getType() == ResourceType.UNKNOWN ? null : I18n.$("file-already-exist");
    }
}
