package thito.nodeflow.java.transform;

import thito.nodeflow.java.*;
import thito.nodeflow.java.util.*;

public class NumberToEnum implements ObjectTransformation {
    private IClass enumType;

    public NumberToEnum(IClass enumType) {
        this.enumType = enumType;
    }

    @Override
    public Reference transform(Reference source) {
        return Array.get(EnumType.Enum(enumType).values(), source);
    }
}
