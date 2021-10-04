package thito.nodeflow.internal.search;

import javafx.beans.value.*;
import thito.nodeflow.internal.ui.editor.*;

public interface SearchableContentProvider {
    ObservableValue<String> iconURLProperty();
    SearchableContentContext createContext(Editor editor);
}
