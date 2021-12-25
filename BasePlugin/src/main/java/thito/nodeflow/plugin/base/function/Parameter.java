package thito.nodeflow.plugin.base.function;

import javafx.beans.property.*;

public class Parameter {
    private StringProperty name = new SimpleStringProperty();
    private ObjectProperty<Type> type = new SimpleObjectProperty<>();
    private ObjectProperty<PortMode> portMode = new SimpleObjectProperty<>();
}
