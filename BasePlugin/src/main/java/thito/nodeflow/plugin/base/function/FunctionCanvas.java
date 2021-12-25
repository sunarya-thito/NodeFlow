package thito.nodeflow.plugin.base.function;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class FunctionCanvas {
    private Function function;
    private FunctionDocs functionDocs;
    private ObservableList<Parameter> parameters = FXCollections.observableArrayList();
}
