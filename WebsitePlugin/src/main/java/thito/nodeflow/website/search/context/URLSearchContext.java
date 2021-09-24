package thito.nodeflow.website.search.context;

import javafx.collections.*;
import thito.nodeflow.internal.search.*;
import thito.nodeflow.internal.ui.editor.*;
import thito.nodeflow.website.search.content.*;

public class URLSearchContext implements SearchableContentContext {
    private SearchableContentProvider provider;
    private Editor editor;
    private ObservableList<URLSearchContent> contents = FXCollections.singletonObservableList(
            new URLSearchContent(this)
    );

    public URLSearchContext(SearchableContentProvider provider, Editor editor) {
        this.provider = provider;
        this.editor = editor;
    }

    @Override
    public SearchableContentProvider getProvider() {
        return provider;
    }

    @Override
    public Editor getEditor() {
        return editor;
    }

    @Override
    public ObservableList<? extends SearchableContent> getSearchableContentList() {
        return contents;
    }
}
