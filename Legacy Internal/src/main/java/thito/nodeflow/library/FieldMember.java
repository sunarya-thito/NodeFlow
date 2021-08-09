package thito.nodeflow.library;

import java.lang.reflect.*;

public class FieldMember extends Member {
    protected TypeCode type;

    public FieldMember(String code) {
        this(new JavaTokenizer(code));
    }

    public FieldMember(JavaTokenizer tokenizer) {
        annotations = tokenizer.eatAnnotations();
        this.modifiers = tokenizer.eatModifiers();
        type = tokenizer.eatType();
        name = tokenizer.eatMemberName();
    }

    public TypeCode getType() {
        return type;
    }

    public void setType(TypeCode type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "FieldMember{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", modifiers=" + Modifier.toString(modifiers) +
                ", type='" + type + '\'' +
                '}';
    }
}
