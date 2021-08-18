package thito.nodeflow.internal.ui.editor;

import javafx.beans.property.*;
import javafx.scene.image.*;
import thito.nodeflow.library.language.*;

public class SearchResultItem {
    private I18n title, source;
    private ObjectProperty<Image> icon = new SimpleObjectProperty<>();

    public SearchResultItem(I18n title, I18n source) {
        this.title = title;
        this.source = source;
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
