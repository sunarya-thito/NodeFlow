package thito.nodeflow.api.project;

import javafx.beans.value.*;
import javafx.collections.*;
import javafx.scene.*;
import thito.nodeflow.api.project.property.*;
import thito.nodeflow.api.resource.*;

import java.util.*;

public interface ProjectTab {
    ObservableList<ComponentProperty<?>> getTabProperties();

    ResourceFile getFile();

    void reloadFile();

    void closeFile();

    boolean isValid();

    String getTitle();

    ObservableValue<String> impl_titleProperty();

    Node impl_getPeer();

    void focus();

    Set<Class<? extends Throwable>> impl_ignoredErrors();
}
