package thito.nodeflow.plugin.base.blueprint;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import thito.nodeflow.plugin.base.blueprint.provider.UnknownNodeProvider;

public class BlueprintManager {
    private static final BlueprintManager manager = new BlueprintManager();

    public static BlueprintManager getManager() {
        return manager;
    }

    private final UnknownNodeProvider unknownNodeProvider = new UnknownNodeProvider();
    private final ObservableList<NodeProviderCategory> categoryList = FXCollections.observableArrayList();

    public ObservableList<NodeProviderCategory> getCategoryList() {
        return categoryList;
    }

    public BlueprintRegistry createRegistry() {
        return new BlueprintRegistry();
    }

    public UnknownNodeProvider getUnknownNodeProvider() {
        return unknownNodeProvider;
    }
}
