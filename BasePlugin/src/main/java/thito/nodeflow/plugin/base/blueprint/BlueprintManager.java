package thito.nodeflow.plugin.base.blueprint;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class BlueprintManager {
    private static final BlueprintManager instance = new BlueprintManager();

    public static BlueprintManager getInstance() {
        return instance;
    }

    private ObservableList<BlueprintViewer> activeBlueprintViewerList = FXCollections.observableArrayList();

    public ObservableList<BlueprintViewer> getActiveBlueprintViewerList() {
        return activeBlueprintViewerList;
    }
}
