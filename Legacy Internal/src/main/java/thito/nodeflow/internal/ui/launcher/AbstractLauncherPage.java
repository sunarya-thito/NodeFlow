package thito.nodeflow.internal.ui.launcher;

import javafx.scene.*;
import thito.nodeflow.api.locale.*;
import thito.nodeflow.api.task.*;
import thito.nodeflow.api.ui.*;
import thito.nodeflow.api.ui.launcher.*;

public abstract class AbstractLauncherPage implements LauncherPage {

    private I18nItem buttonName;
    private I18nItem pageTitle;
    private I18nItem footNote;
    private Icon buttonIcon;
    private Task addButtonAction;
    private PageDisableReason disableReason;

    private LauncherPagePeerUI peer;

    public AbstractLauncherPage(I18nItem buttonName, I18nItem pageTitle, I18nItem footNote, Icon buttonIcon) {
        this.footNote = footNote;
        this.buttonName = buttonName;
        this.pageTitle = pageTitle;
        this.buttonIcon = buttonIcon;
        initializeInterface();
    }

    protected void initializeInterface() {
        peer = new LauncherPagePeerUI(this);
        peer.setId(getClass().getSimpleName());
        peer.getLauncherOverlay().setVisible(false);
    }

    protected void setHeaderEnabled(boolean enabled) {
        if (enabled) {
            peer.getHeader().setManaged(true);
            peer.getHeader().setVisible(true);
        } else {
            peer.getHeader().setManaged(false);
            peer.getHeader().setVisible(false);
        }
    }

    protected void setFooterEnabled(boolean enabled) {
        if (enabled) {
            peer.getFooter().setManaged(true);
            peer.getFooter().setVisible(true);
        } else {
            peer.getFooter().setManaged(false);
            peer.getFooter().setVisible(false);
        }
    }

    protected void loadContent() {
    }

    protected void setAddButtonAction(Task addButtonAction) {
        this.addButtonAction = addButtonAction;
    }

    @Override
    public Task getAddButtonAction() {
        return addButtonAction;
    }

    @Override
    public I18nItem getFootNote() {
        return footNote;
    }

    public LauncherPagePeerUI impl_getPeer() {
        return peer;
    }

    public void show() {
        peer.viewportProperty().set(requestViewport());
    }

    public void hide() {
        peer.viewportProperty().set(null);
    }

    public boolean isShowing() {
        return peer.getLauncherPageViewport().getCenter() != null;
    }

    protected void forceUpdate() {
        if (isShowing()) {
            show();
        }
    }

    @Override
    public I18nItem getButtonName() {
        return buttonName;
    }

    @Override
    public I18nItem getPageTitle() {
        return pageTitle;
    }

    @Override
    public Icon getButtonIcon() {
        return buttonIcon;
    }

    @Override
    public void disablePage(PageDisableReason pageDisableReason) {
        enablePage();
        disableReason = pageDisableReason;
        peer.getOverlayDisableTitle().textProperty().bind(disableReason.getTitle().stringBinding());
        peer.getOverlayDisableMessage().textProperty().bind(disableReason.getMessage().stringBinding());
        peer.getLauncherOverlay().setVisible(true);
    }

    @Override
    public PageDisableReason getDisableReason() {
        return disableReason;
    }

    @Override
    public void enablePage() {
        peer.getOverlayDisableMessage().textProperty().unbind();
        peer.getOverlayDisableTitle().textProperty().unbind();
        peer.getLauncherOverlay().setVisible(false);
    }

    protected abstract Node requestViewport();

}
