package thito.nodeflow.internal.search;

import javafx.collections.ObservableList;
import thito.nodeflow.internal.editor.Editor;

public interface SearchableContentContext {
    SearchableContentProvider getProvider();
    Editor getEditor();
    ObservableList<? extends SearchableContent> getSearchableContentList();
}
