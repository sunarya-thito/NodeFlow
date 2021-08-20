package thito.nodeflow.internal.settings.node;

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

    boolean updating = false;
    public NumberNode(SettingsProperty<? extends Number> item, Number minValue, Number maxValue) {
        super(item);
        spinner = new Spinner<>();
        if (item.getType() == Double.class || item.getType() == Float.class) {
            spinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(minValue == null ? Double.MIN_VALUE : minValue.doubleValue(),
                    maxValue == null ? Double.MAX_VALUE : maxValue.doubleValue()));
            spinner.getValueFactory().setValue(item.get().doubleValue());
        } else {
            spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(minValue == null ? Integer.MIN_VALUE : minValue.intValue(),
                    maxValue == null ? Integer.MAX_VALUE : maxValue.intValue()));
            spinner.getValueFactory().setValue(item.get().intValue());
        }
        item.addListener((obs, old, val) -> {
            if (updating) return;
            updating = true;
            if (item.getType() == Double.class || item.getType() == Float.class) {
                spinner.getValueFactory().setValue(val.doubleValue());
            } else {
                spinner.getValueFactory().setValue(val.intValue());
            }
            updating = false;
        });
        spinner.valueProperty().addListener((obs, old, val) -> {
            if (updating) return;
            updating = true;
            ((SettingsProperty) item).set(numberReBoxing((Number) val, item.getType()));
            updating = false;
        });
        spinner.getStyleClass().add("settings-number");
    }

    @Override
    public Node getNode() {
        return spinner;
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
