package thito.nodeflow.internal.ui.editor;

import javafx.beans.binding.*;
import javafx.collections.*;
import javafx.stage.*;

public class SearchPopup extends Popup {
    private ObservableList<SearchResultItem> searchResultItems = FXCollections.observableArrayList();
    private SearchPopupSkin skin;

    public SearchPopup() {
        skin = new SearchPopupSkin(this);
        skin.visibleProperty().bind(Bindings.isNotEmpty(searchResultItems));
        getContent().add(skin);
    }

    public SearchPopupSkin getSkin() {
        return skin;
    }

    public ObservableList<SearchResultItem> getSearchResultItems() {
        return searchResultItems;
    }
}
