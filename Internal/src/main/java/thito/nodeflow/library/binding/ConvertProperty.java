package thito.nodeflow.library.binding;

import javafx.beans.property.*;

public class ConvertProperty {
    public static BooleanProperty convertBoolean(ObjectProperty<Boolean> booleanObjectProperty) {
        BooleanProperty booleanProperty = new SimpleBooleanProperty();
        booleanProperty.bindBidirectional(booleanObjectProperty);
        return booleanProperty;
    }
    public static IntegerProperty convertToInteger(ObjectProperty<Number> integerObjectProperty) {
        IntegerProperty integerProperty = new SimpleIntegerProperty();
        integerProperty.bindBidirectional(integerObjectProperty);
        return integerProperty;
    }
    public static DoubleProperty convertToDouble(ObjectProperty<Number> numberObjectProperty) {
        DoubleProperty doubleProperty = new SimpleDoubleProperty();
        doubleProperty.bindBidirectional(numberObjectProperty);
        return doubleProperty;
    }

    public static LongProperty convertToLong(ObjectProperty<Number> numberObjectProperty) {
        LongProperty property = new SimpleLongProperty();
        property.bindBidirectional(numberObjectProperty);
        return property;
    }
    public static FloatProperty convertToFloat(ObjectProperty<Number> numberObjectProperty) {
        FloatProperty doubleProperty = new SimpleFloatProperty();
        doubleProperty.bindBidirectional(numberObjectProperty);
        return doubleProperty;
    }
    public static StringProperty convertToString(ObjectProperty<String> stringObjectProperty) {
        StringProperty property = new SimpleStringProperty();
        property.bindBidirectional(stringObjectProperty);
        return property;
    }
}
