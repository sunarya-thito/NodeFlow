package thito.nodeflow.internal.settings;

public abstract class SettingsNodePane<T> extends SettingsNode<T> {
    public SettingsNodePane(SettingsProperty<T> item) {
        super(item);
    }
}
