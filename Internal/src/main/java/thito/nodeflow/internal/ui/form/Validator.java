package thito.nodeflow.internal.ui.form;

import thito.nodeflow.internal.language.*;
import thito.nodeflow.internal.resource.Resource;

import java.io.File;

public interface Validator<T> {
    static <T> Validator<T> mustNotEmpty() {
        return value -> value == null || (value instanceof String && ((String) value).isEmpty()) ? I18n.$("forms.validate-not-empty") : null;
    }
    static Validator<String> validFilename() {
        return value -> {
            try {
                new File(value).getCanonicalPath();
                return null;
            } catch (Throwable t) {
                return I18n.$("forms.validate-invalid-filename");
            }
        };
    }
    static Validator<Resource> resourceExistValidator() {
        return value -> value.exists() ? I18n.$("file-already-exist") : null;
    }
    static <T> Validator<T> combine(Validator<T> a, Validator<T> b) {
        return value -> {
            I18n aValid = a.validate(value);
            return aValid == null ? b.validate(value) : aValid;
        };
    }
    I18n validate(T value);
}
