package thito.nodeflow.library;

import java.lang.reflect.*;
import java.util.*;

public class MethodMember extends Member {
    protected TypeCode type;
    protected FieldMember[] arguments;

    public MethodMember(String code) {
        JavaTokenizer tokenizer = new JavaTokenizer(code);
        annotations = tokenizer.eatAnnotations();
        this.modifiers = tokenizer.eatModifiers();
        generics = tokenizer.eatGenericDeclarations();
        type = tokenizer.eatType();
        name = tokenizer.eatMemberName();
        JavaTokenizer[] args = tokenizer.eatGroup('(', ')').getMembers();
        arguments = Arrays.stream(args).map(FieldMember::new).toArray(FieldMember[]::new);
    }

    public TypeCode getType() {
        return type;
    }

    public void setType(TypeCode type) {
        this.type = type;
    }

    public FieldMember[] getArguments() {
        return arguments;
    }

    public void setArguments(FieldMember[] arguments) {
        this.arguments = arguments;
    }

    @Override
    public String toString() {
        return "MethodMember{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", modifiers=" + Modifier.toString(modifiers) +
                ", type='" + type + '\'' +
                ", arguments=" + Arrays.toString(arguments) +
                '}';
    }
}
