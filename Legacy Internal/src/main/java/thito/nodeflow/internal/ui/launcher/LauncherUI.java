package thito.nodeflow.internal.ui.launcher;

import javafx.animation.*;
import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.util.Duration;
import thito.nodeflow.api.locale.*;
import thito.nodeflow.api.task.*;
import thito.nodeflow.api.ui.*;
import thito.nodeflow.api.ui.launcher.*;
import thito.nodeflow.internal.*;
import thito.nodeflow.library.ui.*;
import thito.nodeflow.library.ui.layout.*;
import thito.nodeflow.library.ui.viewport.*;

public class LauncherUI extends UIComponent {

    private ObservableList<LauncherButton> buttons;
    private Layout pageButtonLayout;
    private ObjectProperty<Node> viewport = new SimpleObjectProperty<>();
    private ObjectProperty<LauncherPage> currentPage = new SimpleObjectProperty<>();
    private LauncherWindow window;

    public LauncherUI(LauncherWindow window) {
        this.window = window;
        currentPage.addListener((obs, old, val) -> setViewport(val));
        this.pageButtonLayout = Layout.loadLayout("LauncherButtonUI");
        setLayout(Layout.loadLayout("LauncherUI"));
    }

    public LauncherPage getCurrentPage() {
        return currentPage.get();
    }

    public ObjectProperty<LauncherPage> currentPageProperty() {
        return currentPage;
    }

    public void setCurrentPage(LauncherPage currentPage) {
        int previous = window.getPages().indexOf(this.currentPage.get());
        int now = window.getPages().indexOf(currentPage);
        ViewportTransition transition = launcherViewport.get().getTransition();
        if (transition instanceof SlideTransition) {
            ((SlideTransition) transition).setDirection(previous < now ? SlideTransition.SlideDirection.BOTTOM : SlideTransition.SlideDirection.TOP);
        }
        window.impl_getPeer().titleProperty().bind(I18n.$("launcher-window-title").stringBinding(currentPage.getPageTitle().stringBinding()));
        this.currentPage.set(currentPage);
    }

    private void initializeDefaultPages() {
        for (LauncherPage page : window.getPages()) {
            getButtons().add(newButton(page));
        }
    }

    public ObservableList<LauncherButton> getButtons() {
        if (buttons == null) {
            buttons = FXCollections.observableArrayList();
        }
        return buttons;
    }

    private LauncherButton newButton(LauncherPage page) {
        return new LauncherButton(pageButtonLayout, this, page);
    }

    private void setViewport(LauncherPage page) {
        viewport.set(page.impl_getPeer());
        page.show();
    }

    @Component("launcher-viewport")
    private final ObjectProperty<ViewportPane> launcherViewport = new SimpleObjectProperty<>();

    @Component("launcher-button-panel")
    private final ObjectProperty<VBox> launcherButtonPanel = new SimpleObjectProperty<>();

    @Override
    protected void onLayoutReady() {
        initializeDefaultPages();
        Toolkit.clip(launcherViewport.get());
        launcherViewport.get().viewportProperty().bind(viewport);
        launcherViewport.get().setTransition(new SlideTransition(
                SlideTransition.SlideDirection.BOTTOM,
                0,
                Duration.seconds(0.5),
                Interpolator.EASE_BOTH,
                true,
                false
        ));
        Bindings.bindContent(launcherButtonPanel.get().getChildren(), getButtons());
        Task.runOnForeground("page-refresh", () -> {
            LauncherButton button = getButtons().get(0);
            button.setSelected(true);
            setCurrentPage(button.getPage());
        });
    }

}
