package thito.nodeflow.api.ui.launcher;

import javafx.scene.Node;
import thito.nodeflow.api.locale.I18nItem;
import thito.nodeflow.api.task.Task;
import thito.nodeflow.api.ui.Icon;

public interface LauncherPage {
    Task getAddButtonAction();

    I18nItem getFootNote();

    I18nItem getButtonName();

    I18nItem getPageTitle();

    Icon getButtonIcon();

    void disablePage(PageDisableReason pageDisableReason);

    void enablePage();

    PageDisableReason getDisableReason();

    Node impl_getPeer();

    void show();

    void hide();

    boolean isShowing();
}
