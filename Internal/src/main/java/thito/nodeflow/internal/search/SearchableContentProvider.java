package thito.nodeflow.internal.search;

import thito.nodeflow.internal.ui.editor.*;

public interface SearchableContentProvider {
    String getIconURL();
    SearchableContentContext createContext(Editor editor);
}
