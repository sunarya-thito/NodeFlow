package thito.nodeflow.internal.search;

import javafx.collections.*;
import thito.nodeflow.internal.language.*;
import thito.nodeflow.internal.ui.editor.*;

public interface SearchSession {
    SearchableContent getContent();
    I18n getName();
    SearchQuery getQuery();
    ObservableList<? extends SearchResult> getResults();
}
