package thito.nodeflow.internal.ui.form;

import thito.nodeflow.internal.language.*;

public interface Validator<T> {
    I18n validate(T value);
}
