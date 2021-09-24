package thito.nodeflow.internal.search;

import javafx.collections.*;
import thito.nodeflow.internal.ui.editor.*;

public interface SearchableContentContext {
    SearchableContentProvider getProvider();
    Editor getEditor();
    ObservableList<? extends SearchableContent> getSearchableContentList();
}
