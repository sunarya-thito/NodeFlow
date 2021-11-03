package thito.nodeflow.internal.search;

import javafx.beans.value.ObservableValue;
import thito.nodeflow.internal.editor.Editor;

public interface SearchableContentProvider {
    ObservableValue<String> iconURLProperty();
    SearchableContentContext createContext(Editor editor);
}
