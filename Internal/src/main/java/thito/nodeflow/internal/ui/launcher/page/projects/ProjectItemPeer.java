package thito.nodeflow.internal.ui.launcher.page.projects;

import javafx.beans.property.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import thito.nodeflow.api.project.*;
import thito.nodeflow.library.ui.*;
import thito.nodeflow.library.ui.layout.*;

import java.util.*;

public class ProjectItemPeer extends UIComponent {
    @Component("title")
    private final ObjectProperty<Label> title = new SimpleObjectProperty<>();

    @Component("location")
    private final ObjectProperty<Label> location = new SimpleObjectProperty<>();

    @Component("root")
    private final ObjectProperty<BorderPane> root = new SimpleObjectProperty<>();

    private ProjectProperties properties;
    private BooleanProperty selected = new SimpleBooleanProperty();
    public ProjectItemPeer(ProjectProperties properties) {
        this.properties = properties;
        setLayout(Layout.loadLayout("ProjectItemUI"));
        setOnMouseClicked(event -> {
            selected.set(true);
        });
    }

    public BooleanProperty selectedProperty() {
        return selected;
    }

    public ProjectProperties getProjectProperties() {
        return properties;
    }

    @Override
    protected void onLayoutReady() {
        title.get().textProperty().bind(properties.impl_nameProperty());
        location.get().setText(properties.getDirectory().getPath());
        Pseudos.install(root.get(), Pseudos.SELECTED, selected);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProjectItemPeer itemPeer = (ProjectItemPeer) o;
        return properties.equals(itemPeer.properties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(properties);
    }
}
