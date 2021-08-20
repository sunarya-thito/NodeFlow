package thito.nodeflow.internal.settings;

public interface SettingsNodeFactory<T> {
    SettingsNode<T> createNode(SettingsProperty<T> item);
}
