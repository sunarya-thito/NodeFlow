package thito.nodeflow.internal.search.content;

import javafx.beans.property.*;
import javafx.collections.*;
import thito.nodeflow.internal.search.*;
import thito.nodeflow.library.language.*;

public abstract class ParsedContent implements SearchableContent {

    public abstract ObservableList<ParsedObject> getParsedObjects();

    @Override
    public SearchSession search(SearchQuery query) {
        return null;
    }

    public interface ParsedObject {
        boolean matches(SearchQuery query);
    }

    public class ParsedSearchSession implements SearchSession {
        @Override
        public I18n getName() {
            return null;
        }

        @Override
        public SearchQuery getQuery() {
            return null;
        }

        @Override
        public ObservableList<SearchResult> getResults() {
            return null;
        }

        public class ParsedSearchResult implements SearchResult {
            @Override
            public SearchableContent getContent() {
                return null;
            }

            @Override
            public BooleanProperty validProperty() {
                return null;
            }

            @Override
            public SearchSession getSession() {
                return null;
            }

            @Override
            public I18n getTitle() {
                return null;
            }

            @Override
            public void navigate() {

            }
        }
    }


}
