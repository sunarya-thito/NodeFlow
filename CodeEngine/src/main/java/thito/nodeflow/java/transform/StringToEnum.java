package thito.nodeflow.java.transform;

import thito.nodeflow.java.*;
import thito.nodeflow.java.util.*;

public class StringToEnum implements ObjectTransformation {
    private IClass target;

    public StringToEnum(IClass target) {
        this.target = target;
    }

    @Override
    public Reference transform(Reference source) {
        return EnumType.Enum(target).valueOf(source);
    }
}
