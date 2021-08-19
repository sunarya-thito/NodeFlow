package thito.nodeflow.internal.search;

import javafx.collections.*;
import thito.nodeflow.library.language.*;

public interface SearchSession {
    I18n getName();
    SearchQuery getQuery();
    ObservableList<SearchResult> getResults();
}
