package thito.nodeflow.internal.settings.node;

import javafx.beans.property.*;
import javafx.scene.*;
import javafx.scene.control.*;
import thito.nodeflow.internal.settings.*;

public class NumberNode extends SettingsNode<Number> {

    public static class Factory implements SettingsNodeFactory<Number> {
        @Override
        public SettingsNode<Number> createNode(SettingsProperty<Number> item) {
            NumberSettings numberSettings = item.getAnnotated(NumberSettings.class);
            return new NumberNode(item, numberSettings == null ? null : numberSettings.min(), numberSettings == null ? null : numberSettings.max());
        }
    }

    private Spinner spinner;
    private ObjectProperty<Number> value = new SimpleObjectProperty<>();

    boolean updating = false;
    public NumberNode(SettingsProperty item, Number minValue, Number maxValue) {
        super(item);
        spinner = new Spinner<>();
        if (item.getType() == Double.class || item.getType() == Float.class) {
            spinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(minValue == null ? Double.MIN_VALUE :
                    Math.max(minValue.doubleValue(), minValue(item.getType()).doubleValue()),
                    maxValue == null ? Double.MAX_VALUE : Math.min(maxValue.doubleValue(), maxValue(item.getType()).doubleValue())));
            spinner.getValueFactory().setValue(getItem().get().doubleValue());
        } else {
            spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(minValue == null ? Integer.MIN_VALUE :
                    Math.max(minValue.intValue(), minValue(item.getType()).intValue()),
                    maxValue == null ? Integer.MAX_VALUE : Math.min(maxValue.intValue(), maxValue(item.getType()).intValue())));
            spinner.getValueFactory().setValue(getItem().get().intValue());
        }
        value.set(numberReBoxing((Number) spinner.getValue(), item.getType()));
        value.addListener((obs, old, val) -> {
            if (updating) return;
            updating = true;
            if (item.getType() == Double.class || item.getType() == Float.class) {
                spinner.getValueFactory().setValue(getItem().get().doubleValue());
            } else {
                spinner.getValueFactory().setValue(getItem().get().intValue());
            }
            updating = false;
        });
        spinner.valueProperty().addListener((obs, old, val) -> {
            if (updating) return;
            updating = true;
            value.set(numberReBoxing((Number) val, item.getType()));
            updating = false;
        });
        spinner.getStyleClass().add("settings-number");
        hasChangedProperty().bind(value.isNotEqualTo(item));
    }

    @Override
    public void apply() {
        getItem().set(value.get());
    }

    @Override
    public Node getNode() {
        return spinner;
    }

    static Number maxValue(Class<?> type) {
        if (type == Integer.class) return Integer.MAX_VALUE;
        if (type == Short.class) return Short.MAX_VALUE;
        if (type == Byte.class) return Byte.MAX_VALUE;
        if (type == Double.class) return Double.MAX_VALUE;
        if (type == Float.class) return Float.MAX_VALUE;
        return Long.MAX_VALUE;
    }

    static Number minValue(Class<?> type) {
        if (type == Integer.class) return Integer.MIN_VALUE;
        if (type == Short.class) return Short.MIN_VALUE;
        if (type == Byte.class) return Byte.MIN_VALUE;
        if (type == Double.class) return Double.MIN_VALUE;
        if (type == Float.class) return Float.MIN_VALUE;
        return Long.MIN_VALUE;
    }

    static Number numberReBoxing(Number from, Class<?> targetType) {
        if (targetType == Integer.class) return from.intValue();
        if (targetType == Double.class) return from.doubleValue();
        if (targetType == Float.class) return from.floatValue();
        if (targetType == Long.class) return from.longValue();
        if (targetType == Short.class) return from.shortValue();
        if (targetType == Byte.class) return from.byteValue();
        return from;
    }
}
