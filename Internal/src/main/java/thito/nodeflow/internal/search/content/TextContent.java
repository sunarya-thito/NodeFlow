package thito.nodeflow.internal.search.content;

import javafx.beans.*;
import javafx.beans.property.*;
import javafx.collections.*;
import thito.nodeflow.internal.search.*;
import thito.nodeflow.internal.language.*;
import thito.nodeflow.internal.ui.editor.*;

import java.util.regex.*;

public abstract class TextContent implements SearchableContent {

    private SearchableContentContext context;

    public TextContent(SearchableContentContext context) {
        this.context = context;
    }

    @Override
    public SearchableContentContext getContext() {
        return context;
    }

    public abstract I18n getName();
    public abstract StringProperty contentProperty();
    protected abstract void requestFocus(int index, int endIndex);

    @Override
    public SearchSession search(SearchQuery query) {
        SearchThread.checkThread();
        return new TextSearchSession(query);
    }

    public class TextSearchSession implements SearchSession, InvalidationListener {
        private SearchQuery query;
        private ObservableList<SearchResult> results = FXCollections.observableArrayList();
        public TextSearchSession(SearchQuery query) {
            this.query = query;
            update();
            contentProperty().addListener(new WeakInvalidationListener(this));
        }

        @Override
        public SearchableContent getContent() {
            return TextContent.this;
        }

        private void update() {
            results.clear();
            String text = contentProperty().get();
            int flags = 0;
            if (query.isIgnoreCase()) {
                flags |= Pattern.CASE_INSENSITIVE;
            }
            if (query.isMultiLine()) {
                flags |= Pattern.MULTILINE;
            }
            Pattern pattern = Pattern.compile(query.isRegex() ? query.getText() : Pattern.quote(text), flags);
            // its recommended to implement this on every searchable content
            // this checks whether the software got a new search query to do
            // and this one is no longer necessary
            if (SearchThread.shouldStop()) return; // the pattern took too long to process and already got a new search query to do
            Matcher matcher = pattern.matcher(text);

            while (!SearchThread.shouldStop() && matcher.find()) {
                int index = matcher.start();
                int endIndex = matcher.end();
                TextSearchResult result = new TextSearchResult(index, endIndex);
                boolean accept = true;
                for (SearchFilter filter : query.getFilters()) {
                    // the previous filter took too long to process and the software got a new search query to do
                    if (!SearchThread.shouldStop()) return;
                    if (!filter.acceptSearch(result)) {
                        accept = false;
                        break;
                    }
                }
                if (accept) {
                    results.add(result);
                }
            }
        }

        @Override
        public void invalidated(Observable observable) {
            SearchThread.submit(this::update);
        }

        @Override
        public I18n getName() {
            return TextContent.this.getName();
        }

        @Override
        public SearchQuery getQuery() {
            return query;
        }

        @Override
        public ObservableList<SearchResult> getResults() {
            return results;
        }

        public class TextSearchResult implements SearchResult, InvalidationListener {
            BooleanProperty valid = new SimpleBooleanProperty();
            int index, endIndex;
            String title;

            public TextSearchResult(int index, int endIndex) {
                this.index = index;
                this.endIndex = endIndex;
                contentProperty().addListener(new WeakInvalidationListener(this));
            }

            @Override
            public SearchableContent getContent() {
                return TextContent.this;
            }

            @Override
            public void invalidated(Observable observable) {
                valid.set(false);
            }

            @Override
            public BooleanProperty validProperty() {
                return valid;
            }

            @Override
            public SearchSession getSession() {
                return TextSearchSession.this;
            }

            @Override
            public I18n getTitle() {
                return I18n.direct(title);
            }

            @Override
            public void navigate() {
                requestFocus(index, endIndex);
            }
        }
    }

}
