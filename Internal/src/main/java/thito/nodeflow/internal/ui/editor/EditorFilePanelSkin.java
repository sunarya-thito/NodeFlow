package thito.nodeflow.internal.ui.editor;

import javafx.scene.image.*;
import thito.nodeflow.internal.plugin.*;
import thito.nodeflow.internal.project.module.*;
import thito.nodeflow.library.resource.*;
import thito.nodeflow.library.ui.*;
import thito.nodeflow.library.ui.resource.*;

import java.io.*;

public class EditorFilePanelSkin extends Skin {

    static ResourceManager resourceManager = new ResourceManager(new File("D:\\Countdown UTBK"));
    @Component("file-explorer")
    ResourceExplorerView explorerView;

    @Override
    protected void onLayoutLoaded() {
        explorerView.sortModeProperty().set(ResourceExplorerView.FILE_TYPE_COMPARATOR.thenComparing(ResourceExplorerView.FILE_NAME_COMPARATOR));
        explorerView.setRoot(new ResourceItem(resourceManager.getRoot()));
        explorerView.setCellFactory(view -> new ResourceCell(explorerView) {
            {
                itemProperty().addListener((obs, old, val) -> {
                    FileModule module = PluginManager.getPluginManager().getModule(val);
                    setGraphic(new ImageView(new Image(module.getIconURL(ThemeManager.getInstance().getTheme()))));
                });
            }
        });
    }
}
