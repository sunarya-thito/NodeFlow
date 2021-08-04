package thito.nodeflow.internal.ui.launcher.page.projects;

import com.jfoenix.controls.*;
import javafx.beans.property.*;
import javafx.scene.layout.*;
import thito.nodeflow.api.locale.*;
import thito.nodeflow.library.ui.layout.*;

@Deprecated
public class ProjectsHeaderPeer extends UIComponent {

    @Component("search")
    private final ObjectProperty<JFXTextField> search = new SimpleObjectProperty<>();

    public ProjectsHeaderPeer() {
        HBox.setHgrow(this, Priority.ALWAYS);
        setLayout(Layout.loadLayout("ProjectsHeaderUI"));
    }

    @Override
    protected void onLayoutReady() {
        search.get().promptTextProperty().bind(I18n.$("projects-search").stringBinding());
    }

    public JFXTextField getSearch() {
        return search.get();
    }
}
