package thito.nodeflow.internal.ui.launcher;

import javafx.beans.property.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import thito.nodeflow.api.locale.*;
import thito.nodeflow.api.ui.launcher.*;
import thito.nodeflow.library.ui.layout.*;

public class LauncherPagePeerUI extends UIComponent {

    @Component("launcher-page-foot-note")
    private final ObjectProperty<Label> footNote = new SimpleObjectProperty<>();

    @Component("launcher-page-viewport")
    private final ObjectProperty<BorderPane> launcherPageViewport = new SimpleObjectProperty<>();

    @Component("launcher-overlay-disable-title")
    private final ObjectProperty<Label> overlayDisableTitle = new SimpleObjectProperty<>();

    @Component("launcher-overlay-disable-message")
    private final ObjectProperty<Label> overlayDisableMessage = new SimpleObjectProperty<>();

    @Component("launcher-overlay")
    private final ObjectProperty<AnchorPane> launcherOverlay = new SimpleObjectProperty<>();

    @Component("launcher-page-foot-buttons")
    private final ObjectProperty<StackPane> footButtons = new SimpleObjectProperty<>();

    @Component("launcher-page-footer")
    private final ObjectProperty<BorderPane> footer = new SimpleObjectProperty<>();

    @Component("launcher-page-header")
    private final ObjectProperty<HBox> header = new SimpleObjectProperty<>();

    private final ObjectProperty<Node> viewport = new SimpleObjectProperty<>();

    private LauncherPage launcherPage;
    public LauncherPagePeerUI(LauncherPage launcherPage) {
        this.launcherPage = launcherPage;
        setLayout(Layout.loadLayout("LauncherPageUI"));
    }

    @Override
    protected void onLayoutReady() {
        LauncherPageContentUI content = new LauncherPageContentUI(launcherPage);
        content.setLayout(Layout.loadLayout("LauncherPageContentUI"));
        content.getViewport().centerProperty().bind(viewport);
        launcherPageViewport.get().setCenter(content);
        I18nItem footNote = launcherPage.getFootNote();
        if (footNote != null) {
            getFootNote().textProperty().bind(footNote.stringBinding());
        }
    }

    public HBox getHeader() {
        return header.get();
    }

    public BorderPane getFooter() {
        return footer.get();
    }

    public StackPane getFootButtons() {
        return footButtons.get();
    }

    public ObjectProperty<Node> viewportProperty() {
        return viewport;
    }

    public AnchorPane getLauncherOverlay() {
        return launcherOverlay.get();
    }

    public Label getOverlayDisableMessage() {
        return overlayDisableMessage.get();
    }

    public Label getOverlayDisableTitle() {
        return overlayDisableTitle.get();
    }

    public BorderPane getLauncherPageViewport() {
        return launcherPageViewport.get();
    }

    public Label getFootNote() {
        return footNote.get();
    }

}
