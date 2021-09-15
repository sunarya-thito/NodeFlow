package thito.nodeflow.internal.project;

import thito.nodeflow.library.language.*;
import thito.nodeflow.library.resource.*;
import thito.nodeflow.library.ui.form.*;

public class AbstractFileValidator implements Validator<Resource> {
    @Override
    public I18n validate(Resource value) {
        return value.getType() == ResourceType.UNKNOWN ? null : I18n.$("file-already-exist");
    }
}
