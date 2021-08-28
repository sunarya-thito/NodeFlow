package thito.nodeflow.library.ui.form;

import thito.nodeflow.library.language.*;

public interface Validator<T> {
    I18n validate(T value);
}
