package thito.nodeflow.internal.search;

import javafx.beans.property.*;
import thito.nodeflow.internal.language.*;

public interface SearchResult {
    SearchableContent getContent();
    BooleanProperty validProperty();
    SearchSession getSession();
    I18n getTitle();
    void navigate();
}
