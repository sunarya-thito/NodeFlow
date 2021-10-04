package thito.nodeflow.internal.ui.editor;

import javafx.scene.control.*;
import javafx.scene.input.*;
import thito.nodeflow.internal.search.*;
import thito.nodeflow.internal.ui.Skin;
import thito.nodeflow.internal.ui.*;

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
            SearchResult result = item.getSearchResult();
            result.navigate();
            result.getContent().getContext().getEditor().getEditorWindow().getStage().requestFocus();
        });
        title.textProperty().bind(item.titleProperty());
        source.textProperty().bind(item.sourceProperty());
        icon.imageProperty().bind(item.iconProperty());
    }
}
