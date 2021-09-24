package thito.nodeflow.website.search.content;

import javafx.beans.property.*;
import javafx.collections.*;
import thito.nodeflow.internal.language.*;
import thito.nodeflow.internal.search.*;

import java.net.*;

public class URLSearchContent implements SearchableContent {

    private SearchableContentContext context;

    public URLSearchContent(SearchableContentContext context) {
        this.context = context;
    }

    @Override
    public SearchableContentContext getContext() {
        return context;
    }

    @Override
    public boolean isInSelection(SearchResult query) {
        return false;
    }

    @Override
    public SearchSession search(SearchQuery query) {
        try {
            URL url = new URL(query.getText());
            return new URLSearchSession(url, query);
        } catch (Throwable t) {
        }
        return null;
    }

    public class URLSearchSession implements SearchSession {
        private URL url;
        private SearchQuery query;
        private I18n name;
        private ObservableList<URLSearchResult> result;
        public URLSearchSession(URL url, SearchQuery query) {
            this.url = url;
            this.query = query;
            this.name = I18n.direct("name");
            this.result = FXCollections.singletonObservableList(new URLSearchResult(url, this));
        }

        @Override
        public SearchableContent getContent() {
            return URLSearchContent.this;
        }

        @Override
        public I18n getName() {
            return this.name;
        }

        @Override
        public SearchQuery getQuery() {
            return query;
        }

        @Override
        public ObservableList<? extends SearchResult> getResults() {
            return result;
        }
    }

    public class URLSearchResult implements SearchResult {
        private URL url;
        private I18n title;
        private BooleanProperty validProperty;
        private SearchSession searchSession;

        public URLSearchResult(URL url, SearchSession searchSession) {
            this.url = url;
            this.searchSession = searchSession;
            this.validProperty = new SimpleBooleanProperty(true);
            this.title = I18n.direct(url.toString());
        }

        @Override
        public SearchableContent getContent() {
            return URLSearchContent.this;
        }

        @Override
        public BooleanProperty validProperty() {
            return validProperty;
        }

        @Override
        public SearchSession getSession() {
            return searchSession;
        }

        @Override
        public I18n getTitle() {
            return title;
        }

        @Override
        public void navigate() {
            // TODO open browser
        }
    }
}
