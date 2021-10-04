package thito.nodeflow.website.search;

import javafx.beans.binding.*;
import javafx.beans.value.*;
import thito.nodeflow.internal.search.*;
import thito.nodeflow.internal.ui.*;
import thito.nodeflow.internal.ui.editor.*;
import thito.nodeflow.website.search.context.*;

public class URLSearchProvider implements SearchableContentProvider {
    public String getIconURL() {
        return "rsrc:Themes/" + ThemeManager.getInstance().getTheme().getName() + "/Icons/GlobeIcon.png";
    }

    @Override
    public ObservableValue<String> iconURLProperty() {
        return Bindings.createStringBinding(this::getIconURL, ThemeManager.getInstance().themeProperty());
    }

    @Override
    public SearchableContentContext createContext(Editor editor) {
        return new URLSearchContext(this, editor);
    }
}
