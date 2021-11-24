package thito.nodeflow.ui.editor;

import javafx.scene.control.*;
import javafx.scene.input.*;
import thito.nodeflow.ui.Component;
import thito.nodeflow.ui.ImagePane;
import thito.nodeflow.ui.Skin;

import javax.naming.directory.SearchResult;

public class SearchResultSkin extends Skin {

    @Component("search-result-title")
    Label title;

    @Component("search-result-source")
    Label source;

    @Component("search-result-icon")
    ImagePane icon;

    private SearchResultItem item;

    public SearchResultSkin(SearchResultItem item) {
        this.item = item;
    }

    @Override
    protected void onLayoutLoaded() {
        addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
//            SearchResult result = item.getSearchResult();
//            result.navigate();
        });
        title.textProperty().bind(item.titleProperty());
        source.textProperty().bind(item.sourceProperty());
        icon.imageProperty().bind(item.iconProperty());
    }
}
