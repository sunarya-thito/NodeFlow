package thito.nodeflow.plugin.base.blueprint;

import javafx.collections.*;
import javafx.scene.paint.*;
import thito.nodeflow.plugin.base.blueprint.provider.*;

import java.util.*;

public class BlueprintManager {
    private static final BlueprintManager blueprintManager = new BlueprintManager();

    public static BlueprintManager getBlueprintManager() {
        return blueprintManager;
    }

    private final UnknownEventNodeProvider unknownEventNodeProvider = new UnknownEventNodeProvider();
    private final UnknownNodeProvider unknownNodeProvider = new UnknownNodeProvider();
    private final ObservableList<EventNodeProvider> eventNodeProviders = FXCollections.observableArrayList();
    private final ObservableList<NodeProvider> nodeProviders = FXCollections.observableArrayList();
    private final Map<Class<?>, Color> typeColorMap = new HashMap<>();

    public Color getTypeColor(Class<?> type) {
        return typeColorMap.computeIfAbsent(type, t -> {
            Random random = new Random();
            return Color.rgb(100 + random.nextInt(155), 100 + random.nextInt(155), 100 + random.nextInt(155));
        });
    }

    public Map<Class<?>, Color> getTypeColorMap() {
        return typeColorMap;
    }

    public UnknownEventNodeProvider getUnknownEventNodeProvider() {
        return unknownEventNodeProvider;
    }

    public UnknownNodeProvider getUnknownNodeProvider() {
        return unknownNodeProvider;
    }

    public ObservableList<EventNodeProvider> getEventNodeProviders() {
        return eventNodeProviders;
    }

    public ObservableList<NodeProvider> getNodeProviders() {
        return nodeProviders;
    }
}
