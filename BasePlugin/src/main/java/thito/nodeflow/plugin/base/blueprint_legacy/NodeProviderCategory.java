package thito.nodeflow.plugin.base.blueprint_legacy;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class NodeProviderCategory {
    private ObservableList<NodeProvider> nodeProviders = FXCollections.observableArrayList();

    public ObservableList<NodeProvider> getNodeProviders() {
        return nodeProviders;
    }
}
