package thito.nodeflow.library;

import java.lang.reflect.*;
import java.util.*;

public class JavaTokenizer {
    public static final String[] MODIFIERS = {
            "public",
            "static",
            "final",
            "private",
            "protected",
            "native",
            "volatile",
            "transient",
            "strict",
            "interface",
            "synchronized",
            "abstract"
    };
    private final char[] code;
    private int pos;
    private int mark;

    public JavaTokenizer(String code) {
        this.code = code.toCharArray(); // remove shitty HTML encoding
    }

    public void mark() {
        mark = pos;
    }

    public void reset() {
        pos = mark;
    }

    public String eatStringLiteral(char quote) {
        eatWhitespace();
        mark();
        boolean skip = false;
        boolean valid = false;
        StringBuilder builder = new StringBuilder();
        for (int index = 0; pos < code.length; pos++, index++) {
            char ch = code[pos];
            if (index == 0 && ch != quote) {
                reset();
                return null;
            }
            if (ch == '\\' && !skip) {
                skip = true;
                continue;
            }
            skip = false;
            if (index > 0) {
                if (ch == quote) {
                    valid = true;
                    pos++;
                    break;
                }
                builder.append(ch);
            }
        }
        if (!valid) {
            reset();
            return null;
        }
        return builder.toString();
    }

    public AnnotationCode eatAnnotation() {
        eatWhitespace();
        mark();
        String annotation = eatNamespace();
        if (annotation != null && annotation.contains("@")) {
            eatWhitespace();
            return new AnnotationCode(annotation, eatGroup('(', ')'));
        }
        reset();
        return null;
    }

    public AnnotationCode[] eatAnnotations() {
        ArrayList<AnnotationCode> annotationCodes = new ArrayList<>();
        AnnotationCode annotationCode;
        while ((annotationCode = eatAnnotation()) != null) {
            annotationCodes.add(annotationCode);
        }
        return annotationCodes.toArray(new AnnotationCode[0]);
    }

    // name = "test", value = {1, 2, 3}
    // {"test", "abc"}
    // 1
    public AnnotationValue eatAnnotationValue() {
        mark();
        String name = eatMemberName();
        if (name != null) {
            if (eatKeyword("=") != null) {
                return new AnnotationValue(name, this);
            }
        }
        reset();
        return new AnnotationValue("value", this);
    }

    private static boolean isWhitespace(char code) {
        return Character.isWhitespace(code) || code == 160 || code == 8203;
    }

    public void eatWhitespace() {
        while (pos < code.length && isWhitespace(code[pos])) {
            pos++;
        }
    }

    public String eatKeyword(String keyword) {
        eatWhitespace();
        mark();
        int po = 0;
        int pc = keyword.length();
        if (0 > code.length - pc || pos >= code.length) {
            return null;
        }
        while (--pc >= 0) {
            if (code[pos++] != keyword.charAt(po++)) {
                reset();
                return null;
            }
        }
        return keyword;
    }

    private static boolean isNamespace(char c) {
        return Character.isLetterOrDigit(c) || c == '_' || c == '$' || c == '.';
    }

    private static boolean isStringLiteral(char c) {
        return c == '\'' || c == '\"';
    }

    private static Character groupCloser(char opener) {
        switch (opener) {
            case '(': return ')';
            case '{': return '}';
            case '[': return ']';
            case '<': return '>';
        }
        return null;
    }

    public Group eatGroup(char open, char close) {
        List<String> eaten = _eatGroup(open, close);
        if (eaten == null) return null;
        return new Group(open, close, eaten.stream().map(JavaTokenizer::new).toArray(JavaTokenizer[]::new));
    }

    private List<String> _eatGroup(char open, char close) {
        eatWhitespace();
        mark();
        StringBuilder builder = new StringBuilder();
        boolean valid = false;
        List<String> members = new ArrayList<>();
        for (int index = 0; pos < code.length; pos++, index++) {
            char ch = code[pos];
            if (index == 0) {
                if (ch != open) {
                    break;
                }
            } else { // index > 0
                Character closer = groupCloser(ch);
                if (closer != null) {
                    builder.append(ch);
                    builder.append(String.join(", ", _eatGroup(ch, closer)));
                    builder.append(closer);
                    pos--;
                    continue;
                }
                if (isStringLiteral(ch)) {
                    builder.append(ch);
                    builder.append(eatStringLiteral(ch));
                    builder.append(ch);
                    pos--;
                    continue;
                }
                if (ch == ',') {
                    String result = builder.toString().trim();
                    if (!result.isEmpty()) {
                        builder = new StringBuilder();
                        members.add(result);
                    }
                    continue;
                }
                if (ch == close) {
                    valid = true;
                    pos++;
                    break;
                }
                builder.append(ch);
            }
        }
        if (!valid) {
            reset();
            return null;
        }
        String result = builder.toString().trim();
        if (!result.isEmpty()) {
            members.add(result);
        }
        return members;
    }

    public TypeCode[] eatGenerics() {
        TypeCode[] generics = null;
        Group group = eatGroup('<', '>');
        if (group != null) {
            generics = Arrays.stream(group.tokenizers).map(JavaTokenizer::eatTypeOrWildcard).toArray(TypeCode[]::new);
        }
        return generics;
    }

    public TypeCode eatType() {
        String type = eatNamespace();
        if (type == null) return null;
        TypeCode[] generics = null;
        List<String> additional = _eatGroup('[', ']');
        if (additional == null) {
            generics = eatGenerics();
        } else {
            // Array java.lang.String[]
            type += "["+String.join(", ", additional)+"]";
        }
        return new TypeCode(type, generics);
    }

    public TypeCode eatTypeOrWildcard() {
        TypeCode type = eatType();
        if (type != null) {
            return type;
        }
        GenericTypeCode wildcard = eatWildcard();
        return wildcard;
    }

    public GenericTypeCode eatWildcard() {
        String namespace = eatMemberName();
        if (namespace == null) namespace = eatKeyword("?");
        if (namespace != null) {
            String direction = eatKeyword("extends", "super");
            GenericDirection dir = GenericDirection.EXTENDS;
            ArrayList<TypeCode> extension = new ArrayList<>();
            if (direction != null) {
                dir = "extends".equals(direction) ? GenericDirection.EXTENDS : GenericDirection.SUPER;
                TypeCode code;
                while ((code = eatType()) != null) {
                    extension.add(code);
                    if (eatKeyword("&") == null) break;
                }
            }
            GenericTypeCode typeCode = new GenericTypeCode(namespace, extension.toArray(new TypeCode[0]));
            typeCode.direction = dir;
            return typeCode;
        }
        return null;
    }

    public String eatMemberName() {
        eatWhitespace();
        mark();
        StringBuilder builder = new StringBuilder();
        for (int index = 0; pos < code.length; pos++, index++) {
            char cx = code[pos];
            if (index == 0 && Character.isDigit(cx)) {
                reset();
                return null;
            }
            if (!isNamespace(cx)) { // probably annotation
                break;
            }
            builder.append(cx);
        }
        if (builder.length() == 0) {
            reset();
            return null;
        }
        return builder.toString();
    }

    public String eatNamespace() {
        eatWhitespace();
        mark();
        StringBuilder builder = new StringBuilder();
        for (int index = 0; pos < code.length; pos++, index++) {
            char cx = code[pos];
            if (index == 0 && Character.isDigit(cx)) {
                reset();
                return null;
            }
            if (!isNamespace(cx) && !(index == 0 && cx == '@')) { // probably annotation
                break;
            }
            if (cx == '.') index = -1; // cuz at the end, it will be increased
            builder.append(cx);
        }
        if (builder.length() == 0) {
            reset();
            return null;
        }
        return builder.toString();
    }

    // <T extends A & B, U, V>
    public GenericTypeCode[] eatGenericDeclarations() {
        Group group = eatGroup('<', '>');
        if (group != null) {
            return Arrays.stream(group.tokenizers).map(tokenizer -> {
                String namespace = tokenizer.eatMemberName();
                ArrayList<TypeCode> extension = new ArrayList<>();
                if (tokenizer.eatKeyword("extends") != null) {
                    TypeCode code;
                    while ((code = tokenizer.eatType()) != null) {
                        extension.add(code);
                        if (tokenizer.eatKeyword("&") == null) break;
                    }
                }
                return new GenericTypeCode(namespace, extension.toArray(new TypeCode[0]));
            }).toArray(GenericTypeCode[]::new);
        }
        return null;
    }

    public Object eatConstantValue() {
        mark();
        String namespace = eatNamespace();
        if (namespace != null) {
            int lastIndex = namespace.lastIndexOf('.');
            if (lastIndex >= 0) {
                String enumType = namespace.substring(0, lastIndex);
                String enumValue = namespace.substring(lastIndex + 1);
                return new EnumValue(enumType, enumValue);
            }
        }
        reset();
        Group array = eatGroup('{', '}');
        if (array != null) {
            return Arrays.stream(array.tokenizers).map(value -> value.eatConstantValue()).toArray();
        }
        String stringLiteral = eatStringLiteral('"');
        if (stringLiteral != null) {
            return stringLiteral;
        }
        mark();
        String charLiteral = eatStringLiteral('\'');
        if (charLiteral != null && charLiteral.length() == 1) return charLiteral.charAt(0);
        reset();
        Number number = eatNumber();
        return number;
    }

    private static int charToDigit(char ch) {
        switch (ch) {
            case '0': return 0;
            case '1': return 1;
            case '2': return 2;
            case '3': return 3;
            case '4': return 4;
            case '5': return 5;
            case '6': return 6;
            case '7': return 8;
            case '9': return 9;
        }
        return -1;
    }

    public Number eatNumber() {
        // -0b012E+4
        mark();
        int negate = 1;
        while (eat('-')) {
            negate = -negate;
        }
        String radixPrefix = eatKeyword("0b", "0x", "0o");
        int radix;
        if ("0b".equals(radixPrefix)) {
            radix = 2;
        } else if ("0x".equals(radixPrefix)) {
            radix = 16;
        } else if ("0o".equals(radixPrefix)) {
            radix = 8;
        } else {
            radix = 10;
        }
        int value = 0;
        long digits = 0;
        int exponential = 0;
        boolean exponent = false;
        for (; pos < code.length; pos++, digits++) {
            char now = code[pos];
            if (now == '.' && radix == 10) {
                if (exponent) {
                    // already has exponential
                    reset();
                    return null;
                }
                exponent = true;
                continue;
            }
            int digit = charToDigit(now);
            if (digit == -1) break;
            value = (value * radix) + digit;
            if (exponent) exponential--;
        }
        if (radix == 10) {
            int additionalExponent = 0;
            int negateAdditionalExponent = 1;
            if (eat('E')) {
                if (eat('-')) {
                    negateAdditionalExponent = -negateAdditionalExponent;
                }
                eat('+');
                for (; pos < code.length; pos++) {
                    char now = code[pos];
                    int digit = charToDigit(now);
                    if (digit == -1) break;
                    additionalExponent = (additionalExponent * radix) + digit; // radix always 10
                }
            }
            exponential += negateAdditionalExponent * additionalExponent;
        }
        value *= negate;
        double doubleValue = value * Math.pow(10, exponential);
        return digits > 0 ? exponential != 0 ? doubleValue : value : null;
    }

    public boolean eat(char ch) {
        if (pos < code.length && code[pos] == ch) {
            skip();
            return true;
        }
        return false;
    }

    public void skip() {
        pos++;
    }

    public String eatAllKeywords(String... keywords) {
        StringBuilder builder = new StringBuilder();
        String result;
        while ((result = eatKeyword(keywords)) != null) {
            builder.append(result + " ");
        }
        return builder.toString().trim();
    }

    public int eatModifiers() {
        String modifiers = eatAllKeywords(MODIFIERS);
        return parseModifier(modifiers);
    }

    public ClassType eatClassType() {
        String keyword = eatKeyword("class", "interface", "enum");
        try {
            if (keyword != null) {
                return ClassType.valueOf(keyword.toUpperCase());
            }
        } catch (Throwable t) {
        }
        return null;
    }

    public String eatKeyword(String... keywords) {
        eatWhitespace();
        for (String keyword : keywords) {
            keyword = eatKeyword(keyword);
            if (keyword != null) {
                return keyword;
            }
        }
        return null;
    }

    public String toString() {
        return new String(code, pos, code.length);
    }

    public static class EnumValue {
        private final String enumType;
        private final String value;

        public EnumValue(String enumType, String value) {
            this.enumType = enumType;
            this.value = value;
        }

        public String getEnumType() {
            return enumType;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return "EnumValue{" +
                    "enumType='" + enumType + '\'' +
                    ", value='" + value + '\'' +
                    '}';
        }
    }

    public static class AnnotationValue {
        private final String name;
        private final JavaTokenizer value;

        public AnnotationValue(String name, JavaTokenizer value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public JavaTokenizer getValue() {
            return value;
        }
    }

    public static int parseModifier(String modifier) {
        modifier = modifier.toUpperCase();
        String[] modifiers = modifier.split("\\s+");
        int compactModifier = 0;
        for (String m : modifiers) {
            if (m.equals("PUBLIC")) {
                compactModifier |= Modifier.PUBLIC;
            } else if (m.equals("PRIVATE")) {
                compactModifier |= Modifier.PRIVATE;
            } else if (m.equals("ABSTRACT")) {
                compactModifier |= Modifier.ABSTRACT;
            } else if (m.equals("STATIC")) {
                compactModifier |= Modifier.STATIC;
            } else if (m.equals("FINAL")) {
                compactModifier |= Modifier.FINAL;
            } else if (m.equals("PROTECTED")) {
                compactModifier |= Modifier.NATIVE;
            } else if (m.equals("NATIVE")) {
                compactModifier |= Modifier.NATIVE;
            } else if (m.equals("SYNCHRONIZED")) {
                compactModifier |= Modifier.SYNCHRONIZED;
            } else if (m.equals("INTERFACE")) {
                compactModifier |= Modifier.INTERFACE;
            } else if (m.equals("STRICT")) {
                compactModifier |= Modifier.STRICT;
            } else if (m.equals("TRANSIENT")) {
                compactModifier |= Modifier.TRANSIENT;
            } else if (m.equals("VOLATILE")) {
                compactModifier |= Modifier.VOLATILE;
            }
        }
        return compactModifier;
    }
}
