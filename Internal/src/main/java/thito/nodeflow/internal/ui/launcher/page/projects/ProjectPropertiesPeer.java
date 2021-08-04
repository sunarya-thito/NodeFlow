package thito.nodeflow.internal.ui.launcher.page.projects;

import javafx.animation.*;
import javafx.beans.property.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.effect.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.util.*;
import thito.nodeflow.api.locale.*;
import thito.nodeflow.api.project.*;
import thito.nodeflow.library.ui.layout.*;

import java.text.*;
import java.util.*;

@Deprecated
public class ProjectPropertiesPeer extends UIComponent {

    private BooleanProperty selected = new SimpleBooleanProperty();

    @Component("buttons")
    private final ObjectProperty<VBox> buttons = new SimpleObjectProperty<>();
    @Component("viewport")
    private final ObjectProperty<VBox> viewport = new SimpleObjectProperty<>();
    @Component("author")
    private final ObjectProperty<Label> author = new SimpleObjectProperty<>();
    @Component("title")
    private final ObjectProperty<Label> title = new SimpleObjectProperty<>();
    @Component("last-modified")
    private final ObjectProperty<Label> lastModified = new SimpleObjectProperty<>();
    @Component("title-popup")
    private final ObjectProperty<Label> titlePopup = new SimpleObjectProperty<>();

    private ProjectsPeer parent;
    private ProjectProperties properties;

    public ProjectPropertiesPeer(ProjectsPeer parent, ProjectProperties properties) {
        this.parent = parent;
        this.properties = properties;
        setLayout(Layout.loadLayout("ProjectPropertiesUI"));
    }

    public ProjectProperties getProjectProperties() {
        return properties;
    }

    private Timeline animation;

    @Override
    protected void onLayoutReady() {
        addEventHandler(MouseEvent.MOUSE_PRESSED, clicked -> {
            setSelected(!isSelected());
        });
        titlePopup.get().textProperty().bind(title.get().textProperty());
        author.get().setText(properties.getAuthor());
        title.get().setText(properties.getName());
        lastModified.get().setText(new SimpleDateFormat(I18n.$("project-properties-last-modified-format").getString(), I18n.impl_getLocalePeer()).format(new Date(properties.getLastModified())));
        getButtons().setScaleX(0.8);
        getButtons().setScaleY(0.8);
        GaussianBlur blur1 = new GaussianBlur(10);
        GaussianBlur blur2 = new GaussianBlur(0);
        getButtons().setEffect(blur1);
        getViewport().setEffect(blur2);
        selected.addListener((obs, old, val) -> {
            if (animation != null) animation.stop();
            if (val) {
                animation = new Timeline(new KeyFrame(Duration.millis(80),
                        new KeyValue(getViewport().opacityProperty(), 0),
                        new KeyValue(getButtons().opacityProperty(), 1),
                        new KeyValue(getViewport().scaleXProperty(), 1.2),
                        new KeyValue(getViewport().scaleYProperty(), 1.2),
                        new KeyValue(getButtons().scaleXProperty(), 1),
                        new KeyValue(getButtons().scaleYProperty(), 1),
                        new KeyValue(blur1.radiusProperty(), 0),
                        new KeyValue(blur2.radiusProperty(), 10)
                        ));
                // unselect others
                for (Node other : parent.getList().getChildren()) {
                    if (other instanceof ProjectPropertiesPeer && other != this) {
                        ((ProjectPropertiesPeer) other).setSelected(false);
                    }
                }
            } else {
                animation = new Timeline(new KeyFrame(Duration.millis(120),
                        new KeyValue(getViewport().opacityProperty(), 1),
                        new KeyValue(getButtons().opacityProperty(), 0),
                        new KeyValue(getViewport().scaleXProperty(), 1),
                        new KeyValue(getViewport().scaleYProperty(), 1),
                        new KeyValue(getButtons().scaleXProperty(), 0.8),
                        new KeyValue(getButtons().scaleYProperty(), 0.8),
                        new KeyValue(blur1.radiusProperty(), 10),
                        new KeyValue(blur2.radiusProperty(), 0)
                        ));
            }
            animation.setOnFinished(e -> animation = null);
            animation.play();
        });
        getViewport().mouseTransparentProperty().bind(getViewport().opacityProperty().isEqualTo(0));
        getButtons().mouseTransparentProperty().bind(getButtons().opacityProperty().isEqualTo(0));
        super.onLayoutReady();
    }

    public VBox getViewport() {
        return viewport.get();
    }

    public VBox getButtons() {
        return buttons.get();
    }

    public boolean isSelected() {
        return selected.get();
    }

    public BooleanProperty selectedProperty() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }
}
