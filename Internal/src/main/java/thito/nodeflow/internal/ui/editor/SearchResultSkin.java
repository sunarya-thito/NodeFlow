package thito.nodeflow.internal.ui.editor;

import javafx.scene.control.*;
import thito.nodeflow.library.ui.Skin;
import thito.nodeflow.library.ui.*;

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
        title.textProperty().bind(item.titleProperty());
        source.textProperty().bind(item.sourceProperty());
        icon.imageProperty().bind(item.iconProperty());
    }
}
