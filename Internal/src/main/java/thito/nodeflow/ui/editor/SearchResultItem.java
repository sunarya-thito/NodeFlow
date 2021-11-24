package thito.nodeflow.ui.editor;

import javafx.beans.property.*;
import javafx.scene.image.*;
import thito.nodeflow.language.I18n;

import javax.naming.directory.SearchResult;

public class SearchResultItem {
    private I18n title, source;
    private ObjectProperty<Image> icon = new SimpleObjectProperty<>();
    private SearchResult searchResult;

    public SearchResultItem(I18n title, I18n source, SearchResult result) {
        this.searchResult = result;
        this.title = title;
        this.source = source;
    }

    public SearchResult getSearchResult() {
        return searchResult;
    }

    public I18n titleProperty() {
        return title;
    }

    public I18n sourceProperty() {
        return source;
    }

    public ObjectProperty<Image> iconProperty() {
        return icon;
    }
}
