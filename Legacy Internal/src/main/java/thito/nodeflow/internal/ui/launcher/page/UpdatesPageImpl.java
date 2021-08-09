package thito.nodeflow.internal.ui.launcher.page;

import com.sandec.mdfx.*;
import javafx.collections.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import thito.nodeflow.api.*;
import thito.nodeflow.api.locale.*;
import thito.nodeflow.api.task.*;
import thito.nodeflow.api.ui.launcher.page.*;
import thito.nodeflow.internal.Toolkit;
import thito.nodeflow.internal.ui.launcher.*;
import thito.nodeflow.library.binding.*;
import thito.nodeflow.library.ui.*;

import java.util.*;

public class UpdatesPageImpl extends AbstractLauncherPage implements UpdatesPage {

    private ObservableList<Update> updates;
    private ObservableList<Version> versions;
    public UpdatesPageImpl() {
        super(I18n.$("launcher-button-updates"), I18n.$("launcher-page-updates"),
                I18n.$("launcher-tip-updates"),
                NodeFlow.getApplication().getResourceManager().getIcon("launcher-button-updates"));
        setHeaderEnabled(false);
    }

    @Override
    public void refreshUpdates() {
        thito.nodeflow.internal.Toolkit.info("updating changelogs");
        NodeFlow.getApplication().getUpdater().fetchChangeLogs().andThen(this::updateLater);
    }

    private void updateLater(List<Update> updates) {
        thito.nodeflow.internal.Toolkit.info("changelogs fetched! waiting for UI update...");
        Task.runOnForeground("update-fetched", () -> {
            thito.nodeflow.internal.Toolkit.info("changelogs refreshed on UI! Total: "+updates.size());
            getUpdates().setAll(updates);
        });
    }

    protected void loadContent() {
        NodeFlow.getApplication().getUpdater().fetchChangeLogs().andThen(this::updateLater);
    }

    public ObservableList<Version> getVersions() {
        if (versions == null) {
            versions = FXCollections.observableArrayList();
        }
        return versions;
    }

    @Override
    public ObservableList<Update> getUpdates() {
        if (updates == null) {
            updates = FXCollections.observableArrayList();
        }
        return updates;
    }

    @Override
    protected Node requestViewport() {
        VBox box = new VBox();
        thito.nodeflow.internal.Toolkit.style(box, "updates-page-content");
        ModernScrollPane scrollPane = new ModernScrollPane();
        MappedListBinding.bind(box.getChildren(), getUpdates(), Version::new);
        thito.nodeflow.internal.Toolkit.style(scrollPane, "updates-page-scroll-pane");
        scrollPane.setContent(box);
        return scrollPane;
    }

    public class VersionTitle extends Pane {

        private Label label;

        private Pane deco = new Pane();

        public VersionTitle(boolean latest, boolean current, String label) {
            this.label = new Label(label);
            thito.nodeflow.internal.Toolkit.style(deco, "updates-page-version-header");
            thito.nodeflow.internal.Toolkit.style(this.label, "updates-page-version-header-text");
            if (current) {
                thito.nodeflow.internal.Toolkit.style(this.label, "updates-page-version-header-text-current");
            } else if (latest) {
                thito.nodeflow.internal.Toolkit.style(this.label, "updates-page-version-header-text-latest");
            } else {
                thito.nodeflow.internal.Toolkit.style(this.label, "updates-page-version-header-text-old");
            }
            deco.layoutXProperty().bind(this.label.widthProperty().add(10));
            deco.layoutYProperty().bind(heightProperty().subtract(deco.heightProperty()).divide(2));
            deco.minWidthProperty().bind(widthProperty().subtract(deco.layoutXProperty()));
            deco.setMinHeight(4);
            this.label.minHeightProperty().bind(heightProperty());
            getChildren().addAll(this.label, deco);
        }

    }

    public class Version extends VBox {

        private VersionTitle title;
        private MDFXNode changes;
        private Update update;
        public Version(Update update) {
            this.update = update;
            boolean current = Objects.equals(update.getVersion(), NodeFlow.getApplication().getVersion());
            boolean latest = update.getVersion().compareTo(NodeFlow.getApplication().getVersion()) > 0;
            String name = update.getVersion().toString();

            title = new VersionTitle(latest, current, name);
            changes = new MDFXNode(update.getChangeLogs());
            changes.getStylesheets().clear();
            thito.nodeflow.internal.Toolkit.style(this, "updates-page-version");
            Toolkit.style(changes, "updates-page-version-text");
            getChildren().addAll(title, changes);
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof Version && ((Version) o).update.equals(update);
        }

        @Override
        public int hashCode() {
            return Objects.hash(update);
        }
    }
}
