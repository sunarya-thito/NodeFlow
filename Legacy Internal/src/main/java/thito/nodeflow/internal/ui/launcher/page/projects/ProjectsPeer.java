package thito.nodeflow.internal.ui.launcher.page.projects;

import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.collections.*;
import javafx.scene.*;
import thito.nodeflow.api.locale.*;
import thito.nodeflow.api.project.*;
import thito.nodeflow.internal.*;
import thito.nodeflow.internal.ui.launcher.page.*;
import thito.nodeflow.library.binding.*;
import thito.nodeflow.library.ui.*;
import thito.nodeflow.library.ui.layout.*;

import java.lang.ref.*;
import java.text.*;
import java.util.*;

@Deprecated
public class ProjectsPeer extends UIContainer {

    @Component("project-list")
    private final ObjectProperty<BetterMasonryPane> list = new SimpleObjectProperty<>();

    private final ProjectsPageImpl page;
    private final ProjectsHeaderPeer headerPeer;


    public ProjectsPeer(ProjectsPageImpl projectsPage, ProjectsHeaderPeer headerPeer) {
        page = projectsPage;
        this.headerPeer = headerPeer;
        setLayout(Layout.loadLayout("ProjectsUI"));
    }

    private List<ProjectProperties> loaded;
    @Override
    protected void initializeContent() {
        loaded = page.getShownProjects();
    }

    @Override
    protected void postLoadContent() {
        updateShown(null);
        super.postLoadContent();
    }

    @Override
    protected void onLayoutReady() {
        headerPeer.getSearch().textProperty().addListener(new WeakReferencedChangeListener<String>(new WeakReference<>(this)) {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                updateShown(newValue);
            }
        });
    }

    public void updateShown(String filter) {
        list.get().getChildren().removeIf(x -> x instanceof ProjectPropertiesPeer && calculateSearchScore(((ProjectPropertiesPeer) x).getProjectProperties(), filter) <= 0);
        for (ProjectProperties prop : loaded) {
            if (list.get().getChildren().stream().filter(x -> x instanceof ProjectPropertiesPeer && ((ProjectPropertiesPeer) x).getProjectProperties() == prop).count() <= 0 &&
                    calculateSearchScore(prop, filter) > 0) {
                list.get().getChildren().add(new ProjectPropertiesPeer(this, prop));
            }
        }
        ObservableList<Node> children = FXCollections.observableArrayList(list.get().getChildren());
        children.sort((o1, o2) -> {
            if (o1 instanceof ProjectPropertiesPeer && o2 instanceof ProjectPropertiesPeer) {
                long scoreA = 0;
                long scoreB = 0;
                if (filter != null) {
                    scoreA = calculateSearchScore(((ProjectPropertiesPeer) o1).getProjectProperties(), filter);
                    scoreB = calculateSearchScore(((ProjectPropertiesPeer) o2).getProjectProperties(), filter);
                }
                return Long.compare(scoreB + ((ProjectPropertiesPeer) o2).getProjectProperties().getLastModified() - Toolkit.DATE_2020, scoreA + ((ProjectPropertiesPeer) o1).getProjectProperties().getLastModified() - Toolkit.DATE_2020);
            }
            return Boolean.compare(o2 instanceof ProjectPropertiesPeer, o1 instanceof ProjectPropertiesPeer);
        });
        list.get().getChildren().setAll(children);
    }

    public long calculateSearchScore(ProjectProperties prop, String filter) {
        if (prop == null || filter == null) return Long.MAX_VALUE;
        String dateModified = new SimpleDateFormat(I18n.$("project-properties-last-modified-format").getString(), I18n.impl_getLocalePeer()).format(new Date(prop.getLastModified()));
        return calculateSearchScore(prop.getName(), filter) * 3 +
                calculateSearchScore(prop.getAuthor(), filter) * 2 +
                calculateSearchScore(dateModified, filter);
    }

    public int calculateSearchScore(String propComparison, String filter) {
        int score = 0;
        if (propComparison.equals(filter)) {
            score += 1000;
        } else if (propComparison.contains(filter)) {
            score += 900;
        } else if (propComparison.equalsIgnoreCase(filter)) {
            score += 800;
        } else if (propComparison.toLowerCase().contains(filter.toLowerCase())) {
            score += 700;
        }
        if (filter.contains(propComparison)) {
            score += 500;
        } else if (filter.toLowerCase().contains(propComparison.toLowerCase())) {
            score += 250;
        }
        return score;
    }

    public BetterMasonryPane getList() {
        return list.get();
    }
}
