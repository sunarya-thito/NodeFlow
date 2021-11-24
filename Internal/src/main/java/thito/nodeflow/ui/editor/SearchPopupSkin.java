package thito.nodeflow.ui.editor;

import javafx.scene.layout.*;
import thito.nodeflow.binding.MappedListBinding;
import thito.nodeflow.ui.Component;
import thito.nodeflow.ui.Skin;

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
