package thito.nodeflow.library.binding;

import javafx.beans.property.*;

import java.util.*;

public class OptionalObjectProperty<T> extends SimpleObjectProperty<T> {

    public Optional<T> getOptional() {
        return Optional.ofNullable(get());
    }
    
}
