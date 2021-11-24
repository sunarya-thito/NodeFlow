package thito.nodeflow.ui.form;

import thito.nodeflow.language.I18n;
import thito.nodeflow.resource.Resource;

import java.io.File;
import java.util.function.Function;

public interface Validator<T> {
    static <T> Validator<T> mustNotEmpty() {
        return value -> value == null || (value instanceof String && ((String) value).isEmpty()) ? I18n.$("forms.validate-not-empty") : null;
    }
    static Validator<String> validFilename() {
        return value -> {
            if (value == null || value.isEmpty()) return null;
            try {
                new File(value).getCanonicalPath();
                return null;
            } catch (Throwable t) {
                return I18n.$("forms.validate-invalid-filename");
            }
        };
    }
    static Validator<Resource> resourceMustNotExist() {
        return value -> value.exists() ? I18n.$("file-already-exist") : null;
    }
    static Validator<Resource> pathNotExist() {
        return value -> !value.exists() ? I18n.$("forms.validate-path-not-exist") : null;
    }
    static <T> Validator<T> combine(Validator<T> a, Validator<T> b) {
        return value -> {
            I18n aValid = a.validate(value);
            return aValid == null ? b.validate(value) : aValid;
        };
    }
    I18n validate(T value);
    default Validator<T> combine(Validator<T> other) {
        return combine(this, other);
    }
    default <K> Validator<K> map(Function<K, T> function) {
        return value -> validate(function.apply(value));
    }
}
