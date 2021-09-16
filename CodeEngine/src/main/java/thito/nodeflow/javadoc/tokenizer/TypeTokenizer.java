package thito.nodeflow.javadoc.tokenizer;

import org.apache.commons.text.*;
import thito.nodeflow.javadoc.*;

import java.lang.reflect.*;
import java.util.*;

public class TypeTokenizer {
    private int index;
    private char[] array;

    public int getIndex() {
        return index;
    }

    public String toString() {
        return new String(array, 0, index) + "<index:"+index+">" + new String(array, index, array.length - index);
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public TypeTokenizer(int index, char[] array) {
        this.index = index;
        this.array = array;
    }

    public boolean hasNext() {
        return index < array.length;
    }

    public Object eatLiteral() {
        if (eat('{')) {
            List<Object> list = new ArrayList<>();
            while (index < array.length) {
                eatWhitespace();
                list.add(eatLiteral());
                eatWhitespace();
                if (!eat(',')) {
                    break;
                }
            }
            if (!eat('}')) throw new IllegalArgumentException("invalid literal token: "+new String(array, 0, index + 1)+" "+list);
            return list.toArray(new Object[0]);
        }
        if (eat("null")) return null;
        String string = eatLiteralString();
        if (string != null) return string;
        if (eat("true")) return true;
        if (eat("false")) return true;
        Number l = eatLong();
        if (l != null) return l;
        Number d = eatDouble();
        if (d != null) return d;
        return new UnknownValue(eatTypeName());
    }

    public ClassType eatClassType() {
        boolean tryInterface = eat('@');
        String name = eatName();
        try {
            ClassType classType = ClassType.valueOf(name.toUpperCase());
            if (tryInterface && classType != ClassType.INTERFACE) {
                index -= name.length() + 1;
                return null;
            }
            return classType;
        } catch (Throwable t) {
            if (name != null) {
                index -= name.length();
                if (tryInterface) index--;
            }
        }
        return null;
    }

    public int eatModifiers() {
        int mod = 0;
        while (index < array.length) {
            eatWhitespace();
            String name = eatName();
            if (name != null) {
                Integer m = switch (name) {
                    case "public" -> Modifier.PUBLIC;
                    case "private" -> Modifier.PRIVATE;
                    case "protected" -> Modifier.PROTECTED;
                    case "static" -> Modifier.STATIC;
                    case "abstract" -> Modifier.ABSTRACT;
                    case "transient" -> Modifier.TRANSIENT;
                    case "strictfp" -> Modifier.STRICT;
                    case "final" -> Modifier.FINAL;
                    case "volatile" -> Modifier.VOLATILE;
                    case "synchronized" -> Modifier.SYNCHRONIZED;
                    case "native" -> Modifier.NATIVE;
//                    case "interface" -> Modifier.INTERFACE;
                    default -> null;
                };
                if (m == null) {
                    index -= name.length();
                    break;
                }
                mod |= m;
            } else break;
        }
        return mod;
    }

    public static class UnknownValue {
        private String type = "Unknown";
        private Object object;

        public UnknownValue(Object object) {
            this.object = object;
        }

        @Override
        public String toString() {
            return String.valueOf(object);
        }
    }

    public Number eatLong() {
        StringBuilder builder = new StringBuilder();
        boolean mustLong = false, mustFloat = false, mustDouble = false;
        while (index < array.length) {
            char c = array[index];
            if (Character.isDigit(c)) {
                builder.append(c);
            } else if (c != '_') {
                if (c == 'l' || c == 'L') {
                    index++;
                    mustLong = true;
                    break;
                }
                if (c == 'd' || c == 'D') {
                    index++;
                    mustDouble = true;
                    break;
                }
                if (c == 'f' || c == 'F') {
                    index++;
                    mustFloat = true;
                    break;
                }
                if (c == '+' && builder.length() <= 0) {
                    index++;
                    continue;
                }
                if (c == '-' && builder.length() <= 0) {
                    index++;
                    continue;
                }
                break;
            }
            index++;
        }
        if (!builder.isEmpty()) {
            long l = Long.parseLong(builder.toString());
            if (!mustLong && l >= Integer.MIN_VALUE && l <= Integer.MAX_VALUE) {
                return (int) l;
            }
            if (mustFloat) {
                return (float) l;
            }
            if (mustDouble) {
                return (double) l;
            }
            return l;
        }
        return null;
    }

    public Number eatDouble() {
        StringBuilder builder = new StringBuilder();
        Double lastValidValue = null;
        while (index < array.length) {
            try {
                if (array[index] == 'd' || array[index] == 'D') {
                    index++;
                    return lastValidValue;
                }
                if (array[index] == 'f' || array[index] == 'F') {
                    index++;
                    return Objects.requireNonNull(lastValidValue).floatValue();
                }
                String s = builder.append(array[index]).toString();
                if (s.endsWith(".") || s.endsWith("-") || s.endsWith("+") || s.endsWith("E") || s.endsWith("e")) s += '0';
                lastValidValue = Double.parseDouble(s);
                index++;
            } catch (Throwable t) {
                break;
            }
        }
        return lastValidValue;
    }

    public String eatName() {
        StringBuilder builder = new StringBuilder();
        while (index < array.length) {
            char ch = array[index];
            if (builder.length() == 0) {
                if (Character.isDigit(ch)) {
                    break;
                }
            }
            if (!Character.isLetterOrDigit(ch) && ch != '$' && ch != '_') {
                break;
            }
            builder.append(ch);
            index++;
        }
        return builder.isEmpty() ? null : builder.toString();
    }

    public String eatTypeName() {
        StringBuilder builder = new StringBuilder();
        while (hasNext()) {
            int mark = index;
            String name = eatName();
            if (name == null) {
                index = mark;
                break;
            }
            builder.append(name);
            mark = index;
            if (eat("...")) {
                index = mark;
                break;
            }
            if (!eat('.')) {
                break;
            }
            builder.append('.');
        }
        return builder.isEmpty() ? null : builder.toString();
    }

    public ArrayDimensionDeclaration eatArray() {
        int mark = index;
        JavaAnnotation[] annotations = eatAnnotations().toArray(new JavaAnnotation[0]);
        eatWhitespace();
        if (eat('[')) {
            eatWhitespace();
            if (eat(']')) {
                ArrayDimensionDeclaration declaration = new ArrayDimensionDeclaration();
                declaration.setAnnotations(annotations);
                return declaration;
            }
        }
        index = mark;
        return null;
    }

    public ArrayDimensionDeclaration[] eatArrays() {
        List<ArrayDimensionDeclaration> dimensions = new ArrayList<>();
        while (index < array.length) {
            eatWhitespace();
            ArrayDimensionDeclaration declaration = eatArray();
            if (declaration == null) break;
            dimensions.add(declaration);
        }
        return dimensions.toArray(new ArrayDimensionDeclaration[0]);
    }

    public boolean eatVarArgs() {
        return eat("...");
    }

    public int eatWhitespace() {
        int count = 0;
        while (index < array.length) {
            char ch = array[index];
            if (!Character.isWhitespace(ch)) {
                break;
            }
            index++;
            count++;
        }
        return count;
    }

    public TypeReference[] eatSplit(char delimiter) {
        List<TypeReference> references = new ArrayList<>();
        while (index < array.length) {
            eatWhitespace();
            TypeReference ref = eatType();
            if (ref == null) break;
            references.add(ref);
            eatWhitespace();
            if (index >= array.length) break;
            char ch = array[index];
            if (ch == delimiter) {
                index++;
            } else break;
        }
        return references.isEmpty() ? null : references.toArray(new TypeReference[0]);
    }

    public TypeReference[] eatGenericSplit(char delimiter) {
        List<TypeReference> references = new ArrayList<>();
        while (index < array.length) {
            eatWhitespace();
            TypeReference ref = eatGenericVariable();
            if (ref == null) break;
            references.add(ref);
            eatWhitespace();
            if (index >= array.length) break;
            char ch = array[index];
            if (ch == delimiter) {
                index++;
            } else break;
        }
        return references.isEmpty() ? null : references.toArray(new TypeReference[0]);
    }

    public String eatLiteralString() {
        if (!eat('"')) return null;
        StringBuilder builder = new StringBuilder();
        while (index < array.length) {
            boolean escape = index - 1 >= 0 && array[index - 1] == '\\';
            if (!escape && eat('"')) return StringEscapeUtils.unescapeJava(builder.toString());
            builder.append(array[index]);
            index++;
        }
        return null;
    }

    public boolean eat(char c) {
        if (index < array.length) {
            if (array[index] == c) {
                index++;
                return true;
            }
        }
        return false;
    }

    public boolean eat(String text) {
        for (int i = 0; i < text.length(); i++) {
            int targetIndex = index + i;
            if (targetIndex >= array.length) return false;
            char c = array[targetIndex];
            if (c != text.charAt(i)) return false;
        }
        index += text.length();
        return true;
    }

    public List<JavaAnnotation> eatAnnotations() {
        List<JavaAnnotation> annotations = new ArrayList<>();
        while (hasNext()) {
            eatWhitespace();
            JavaAnnotation annotation = eatAnnotation();
            if (annotation == null) break;
            annotations.add(annotation);
        }
        return annotations;
    }

    public LocalFieldDeclaration eatLocalField() {
        int mark = index;
        JavaAnnotation[] annotations = eatAnnotations().toArray(new JavaAnnotation[0]);
        if (annotations.length <= 0 || eatWhitespace() > 0) {
            eatWhitespace();
            String name = eatName();
            if (name != null) {
                LocalFieldDeclaration declaration = new LocalFieldDeclaration();
                declaration.setAnnotations(annotations);
                declaration.setName(name);
                return declaration;
            }
        }
        index = mark;
        return null;
    }

    public JavaMethod.Parameter eatParameter() {
        int mark = index;
        eatWhitespace();
        TypeReference type = eatType();
        eatWhitespace();
        boolean varArgs = eatVarArgs();
        eatWhitespace();
        LocalFieldDeclaration name = eatLocalField();
        if (name != null) {
            JavaMethod.Parameter parameter = new JavaMethod.Parameter();
            parameter.setVarargs(varArgs);
            parameter.setName(name);
            parameter.setType(type);
            return parameter;
        }
        index = mark;
        return null;
    }

    // ? extends B
    // B
    // B<C>
    // B<? extends C>
    // B<? super C>
    // B[]
    // B<C>[]
    // B#C
    public TypeReference eatType() {
        JavaAnnotation[] annotations = eatAnnotations().toArray(new JavaAnnotation[0]);
        eatWhitespace();
        if (eat('?')) {
            WildcardTypeReference typeReference = new WildcardTypeReference();
            int mark = index;
            if (eatWhitespace() > 0) {
                if (eat("extends")) {
                    if (eatWhitespace() > 0) {
                        TypeReference[] references = eatSplit('&');
                        if (references == null) {
                            index = mark;
                        } else {
                            typeReference.setUpperBounds(references);
                        }
                    } else index = mark;
                } else if (eat("super")) {
                    if (eatWhitespace() > 0) {
                        TypeReference[] references = eatSplit('&');
                        if (references == null) {
                            index = mark;
                        } else {
                            typeReference.setLowerBounds(references);
                        }
                    } else index = mark;
                }
            }
            typeReference.setAnnotations(annotations);
            return typeReference;
        } else {
            String typeName = eatTypeName();
            if (typeName != null) {
                ClassTypeReference typeReference = new ClassTypeReference(typeName);
                int mark = index;
                eatWhitespace();
                if (eat('<')) {
                    eatWhitespace();
                    TypeReference[] references = eatSplit(',');
                    if (references != null) {
                        if (eat('>')) {
                            typeReference.setParameters(references);
                        } else index = mark;
                    } else index = mark;
                } else if (eat('#')) {
                    eatWhitespace();
                    String name = eatName();
                    if (name != null) {
                        VariableTypeReference reference = new VariableTypeReference();
                        reference.setOwner(typeName);
                        reference.setName(name);
                        reference.setArrayDimensions(eatArrays());
                        reference.setAnnotations(annotations);
                        return reference;
                    }
                } else index = mark;
                typeReference.setAnnotations(annotations);
                typeReference.setArrayDimensions(eatArrays());
                return typeReference;
            }
        }
        return null;
    }

    // A
    // A extends B
    // A super B & C
    public VariableTypeReference eatGenericVariable() {
        String name = eatName();
        if (name != null && !name.isEmpty()) {
            VariableTypeReference typeReference = new VariableTypeReference();
            typeReference.setName(name);
            int mark = index;
            if (eatWhitespace() > 0) {
                if (eat("extends")) {
                    if (eatWhitespace() > 0) {
                        TypeReference[] references = eatSplit('&');
                        if (references == null) {
                            index = mark;
                        } else {
                            typeReference.setUpperBounds(references);
                        }
                    } else index = mark;
                } else if (eat("super")) {
                    if (eatWhitespace() > 0) {
                        TypeReference[] references = eatSplit('&');
                        if (references == null) {
                            index = mark;
                        } else {
                            typeReference.setLowerBounds(references);
                        }
                    } else index = mark;
                }
            }
            return typeReference;
        }
        return null;
    }

    public JavaAnnotation eatAnnotation() {
        if (eat('@')) {
            String type = eatTypeName();
            if (type != null) {
                JavaAnnotation javaAnnotation = new JavaAnnotation();
                javaAnnotation.setType(type);
                eatWhitespace();
                List<JavaAnnotation.Value> values = new ArrayList<>();
                if (eat('(')) {
                    while (hasNext()) {
                        eatWhitespace();
                        Object name = eatLiteral();
                        if (name != null) {
                            eatWhitespace();
                            eat("()");
                            eatWhitespace();
                            if (eat('=')) {
                                eatWhitespace();
                                Object value = eatLiteral();
                                JavaAnnotation.Value val = new JavaAnnotation.Value(String.valueOf(name), value);
                                values.add(val);
                            } else {
                                values.add(new JavaAnnotation.Value("value", name));
                            }
                        } else break;
                        eatWhitespace();
                        if (!eat(',')) break;
                    }
                }
                eatWhitespace();
                if (eat(')')) {
                    javaAnnotation.setValues(values.toArray(new JavaAnnotation.Value[0]));
                }
                return javaAnnotation;
            }
        }
        return null;
    }
}
