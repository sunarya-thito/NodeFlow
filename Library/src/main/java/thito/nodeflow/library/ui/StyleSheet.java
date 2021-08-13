package thito.nodeflow.library.ui;

import javafx.beans.property.*;
import javafx.collections.*;

public class StyleSheet {
    private StringProperty layout = new SimpleStringProperty();
    private ObservableList<String> cssFiles = FXCollections.observableArrayList();

    public StringProperty layoutProperty() {
        return layout;
    }

    public ObservableList<String> getCssFiles() {
        return cssFiles;
    }
}
