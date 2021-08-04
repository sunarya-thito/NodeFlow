package thito.nodeflow.internal.editor.config.type;

import javafx.beans.property.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.util.*;
import thito.nodeflow.api.config.*;
import thito.nodeflow.api.locale.*;
import thito.nodeflow.internal.editor.config.*;
import thito.nodeflow.library.binding.*;
import thito.reflectedbytecode.*;

public class NumberValueType implements ConfigValueType {
    @Override
    public String getId() {
        return "number";
    }

    @Override
    public I18nItem getDisplayName() {
        return I18n.$("config-number-type");
    }

    @Override
    public Class<?> getFieldType() {
        return Number.class;
    }

    @Override
    public ConfigValueHandler createHandler(Object value) {
        Number number;
        if (value instanceof Number) {
            number = (Number) value;
        } else {
            String stringified = String.valueOf(value);
            try {
                number = Long.parseLong(stringified);
            } catch (Throwable t) {
                try {
                    number = Double.parseDouble(stringified);
                } catch (Throwable t2) {
                    number = 0;
                }
            }
        }
        Number finalNumber = number;
        return new ConfigValueHandler() {
            private ObjectProperty<Object> object = new SimpleObjectProperty<>(finalNumber);
            private Spinner<Number> spinner = new Spinner<>();

            {
                spinner.getStyleClass().add("number-value-type");
                spinner.setEditable(true);
                spinner.setValueFactory(new SpinnerValueFactory<Number>() {
                    {
                        setConverter(new StringConverter<Number>() {
                            @Override
                            public String toString(Number object) {
                                return object == null ? "0" : object.toString();
                            }

                            @Override
                            public Number fromString(String string) {
                                try {
                                    return Long.parseLong(string);
                                } catch (Throwable t) {
                                    try {
                                        return Double.parseDouble(string);
                                    } catch (Throwable t2) {
                                        return 0;
                                    }
                                }
                            }
                        });
                    }
                    @Override
                    public void decrement(int steps) {
                        if (getValue() instanceof Double) {
                            setValue(getValue().doubleValue() - steps);
                        } else if (getValue() instanceof Float) {
                            setValue(getValue().floatValue() - steps);
                        } else {
                            setValue(getValue().longValue() - steps);
                        }
                    }

                    @Override
                    public void increment(int steps) {
                        if (getValue() instanceof Double) {
                            setValue(getValue().doubleValue() + steps);
                        } else if (getValue() instanceof Float) {
                            setValue(getValue().floatValue() + steps);
                        } else {
                            setValue(getValue().longValue() + steps);
                        }
                    }

                });
                MappedBidirectionalBinding.bindBidirectional(spinner.getValueFactory().valueProperty(), object,
                        x -> x, x -> x instanceof Number ? (Number) x : 0);
            }

            @Override
            public ObjectProperty<Object> valueProperty() {
                return object;
            }

            @Override
            public ConfigValueType getType() {
                return NumberValueType.this;
            }

            @Override
            public void save(String key, Section section) {
                section.set(key, object.get());
            }

            @Override
            public Node impl_getPeer() {
                return spinner;
            }

            @Override
            public Reference getCompilableReference() {
                return Reference.javaToReference(object.get());
            }

        };
    }
}
