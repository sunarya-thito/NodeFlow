package thito.nodeflow.api.ui.dialog.button;

import thito.nodeflow.api.locale.I18nItem;
import thito.nodeflow.api.ui.Icon;

public interface TextDialogButton extends DialogButton {
    I18nItem getLabel();

    void setLabel(I18nItem label);

    Icon getIcon();

    void setIcon(Icon icon);

    @Override
    TextButtonPeer createPeer();
}
