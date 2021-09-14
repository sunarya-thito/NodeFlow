package thito.nodeflow.java.util;

import thito.nodeflow.java.*;

public class EnumType {

    public static EnumType Enum(IClass enumType) {
        return new EnumType(enumType);
    }

    private IClass enumType;

    public EnumType(IClass enumType) {
        this.enumType = enumType;
    }

    public IClass getEnumType() {
        return enumType;
    }

    public Value valueOf(String name) {
        return new Value(name);
    }

    public Reference values() {
        return enumType.method("values").invoke();
    }

    public class Value extends Reference {
        private String name;
        public Value(String name) {
            super(getEnumType());
            this.name = name;
        }

        public String name() {
            return name;
        }

        public Reference ordinal() {
            return method("ordinal").invoke();
        }

        @Override
        public void writeByteCode() {
            getType().field(name).writeByteCode();
        }

        @Override
        public void writeSourceCode() {
            SourceCode code = SourceCode.getContext();
            code.getLine().append(code.simplifyType(getEnumType()))
                    .append('.')
                    .append(name);
        }
    }
}
