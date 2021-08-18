package thito.nodeflow.internal.ui.editor;

import javafx.scene.layout.*;
import thito.nodeflow.library.binding.*;
import thito.nodeflow.library.ui.*;

public class SearchPopupSkin extends Skin {
    @Component("content")
    Pane box;

    private SearchPopup popup;

    public SearchPopupSkin(SearchPopup popup) {
        this.popup = popup;
    }

    @Override
    protected void onLayoutLoaded() {
        MappedListBinding.bind(box.getChildren(), popup.getSearchResultItems(), SearchResultSkin::new);
    }
}
