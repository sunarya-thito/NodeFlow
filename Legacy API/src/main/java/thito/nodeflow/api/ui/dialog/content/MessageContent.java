package thito.nodeflow.api.ui.dialog.content;

import thito.nodeflow.api.locale.I18nItem;
import thito.nodeflow.api.ui.Pos;

public interface MessageContent extends TitledContent {
    I18nItem getMessage();

    void setMessage(I18nItem message);

    Pos getMessageAlignment();

    void setMessageAlignment(Pos pos);
}
