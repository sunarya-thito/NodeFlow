package thito.nodeflow.library.binding;

import javafx.beans.property.*;
import javafx.beans.value.*;
import thito.nodeflow.library.task.*;

public class ThreadBinding {
    public static <T> void bind(WritableValue<T> property, ObservableValue<T> source, TaskThread thread) {
        if (property instanceof Property) {
            ((Property<T>) property).unbind();
        }
        thread.schedule(() -> {
            property.setValue(source.getValue());
            source.addListener(new WeakReferencedListener(obs -> {
                thread.schedule(() -> {
                    property.setValue(source.getValue());
                });
            }, property));
        });
    }
}
