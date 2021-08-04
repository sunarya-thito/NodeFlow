package thito.nodeflow.internal.ui.launcher;

import javafx.beans.property.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import thito.nodeflow.api.*;
import thito.nodeflow.api.ui.launcher.*;
import thito.nodeflow.library.ui.*;
import thito.nodeflow.library.ui.layout.*;

import java.util.logging.*;

public class LauncherButton extends UIComponent {

    private LauncherUI ui;
    private LauncherPage page;
    private BooleanProperty selected;

    public LauncherButton(Layout layout, LauncherUI ui, LauncherPage page) {
        this.ui = ui;
        this.page = page;
        setLayout(layout);
    }

    public LauncherPage getPage() {
        return page;
    }

    protected BooleanProperty selectedProperty() {
        return selected == null ? selected = new SimpleBooleanProperty() : selected;
    }

    @Override
    protected void setup(Node node) {
        Pseudos.install(node, Pseudos.SELECTED, selectedProperty());
        super.setup(node);
    }

    @Override
    protected void onClick(MouseEvent event) {
        NodeFlow.getApplication()
                .getLogger().log(Level.INFO, "Page changed to "+page.getClass().getName());
        for (LauncherButton other : ui.getButtons()) {
            if (other != this) {
                other.setSelected(false);
            }
        }
        setSelected(true);
        ui.setCurrentPage(page);
        super.onClick(event);
    }

    public boolean isSelected() {
        return selectedProperty().get();
    }

    public void setSelected(boolean selected) {
        selectedProperty().set(selected);
    }

    @Component("launcher-button-icon")
    private final ObjectProperty<ImageView> launcherButtonIcon = new SimpleObjectProperty<>();

    @Component("launcher-button-text")
    private final ObjectProperty<Label> launcherButtonText = new SimpleObjectProperty<>();

    @Override
    protected void onLayoutReady() {
        launcherButtonIcon.get().imageProperty().bind(page.getButtonIcon().impl_propertyPeer());
        launcherButtonText.get().textProperty().bind(page.getButtonName().stringBinding());
    }

}
