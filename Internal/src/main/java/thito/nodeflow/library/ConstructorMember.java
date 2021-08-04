package thito.nodeflow.library;

import java.lang.reflect.*;
import java.util.*;

public class ConstructorMember extends Member {
    protected FieldMember[] arguments;

    public ConstructorMember(String code) {
        JavaTokenizer tokenizer = new JavaTokenizer(code);
        annotations = tokenizer.eatAnnotations();
        this.modifiers = tokenizer.eatModifiers();
        generics = tokenizer.eatGenericDeclarations();
        name = tokenizer.eatMemberName();
        JavaTokenizer[] args = tokenizer.eatGroup('(', ')').getMembers();
        arguments = Arrays.stream(args).map(FieldMember::new).toArray(FieldMember[]::new);
    }

    public FieldMember[] getArguments() {
        return arguments;
    }

    public void setArguments(FieldMember[] arguments) {
        this.arguments = arguments;
    }

    @Override
    public String toString() {
        return "ConstructorMember{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", modifiers=" + Modifier.toString(modifiers) +
                ", arguments=" + Arrays.toString(arguments) +
                '}';
    }
}
