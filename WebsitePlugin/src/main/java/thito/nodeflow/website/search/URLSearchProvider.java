package thito.nodeflow.website.search;

import thito.nodeflow.internal.search.*;
import thito.nodeflow.internal.ui.*;
import thito.nodeflow.internal.ui.editor.*;
import thito.nodeflow.website.search.context.*;

public class URLSearchProvider implements SearchableContentProvider {
    @Override
    public String getIconURL() {
        return "rsrc:Themes/" + ThemeManager.getInstance().getTheme().getName() + "/Icons/Globe.png";
    }

    @Override
    public SearchableContentContext createContext(Editor editor) {
        return new URLSearchContext(this, editor);
    }
}
