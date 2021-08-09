package thito.nodeflow.api.ui.dialog.content;

import javafx.scene.layout.Region;
import thito.nodeflow.api.locale.I18nItem;
import thito.nodeflow.api.ui.Pos;
import thito.nodeflow.api.ui.dialog.Dialog;

public interface TitledContent extends DialogContent {
    I18nItem getHeader();

    void setHeader(I18nItem title);

    Pos getHeaderAlignment();

    void setHeaderAlignment(Pos pos);

    Dialog.Type getType();

    void setType(Dialog.Type type);

    Dialog.Level getLevel();

    void setLevel(Dialog.Level level);

    Region impl_createContentPeer();
}
