package thito.nodeflow.library.ui.layout;

import javafx.geometry.*;

public class LayoutHelper {

    public static String generalizeEnumName(String name) {
        return name.toUpperCase().replace('-', '_');
    }

    public static Pos parsePos(String value) throws LayoutParserException {
        return parseEnum(Pos.class, value, true);
    }

    public static Insets parseInsets(String value) throws LayoutParserException {
        String[] split = value.split(" ");
        if (split.length == 1) { // all sides
            return new Insets(parseDouble(split[0]));
        }
        if (split.length == 2) { // top-bottom and left-right
            double topBottom = parseDouble(split[0]);
            double leftRight = parseDouble(split[1]);
            return new Insets(topBottom, leftRight, topBottom, leftRight);
        }
        if (split.length >= 4) {
            return new Insets(parseDouble(split[0]), parseDouble(split[1]), parseDouble(split[2]), parseDouble(split[3]));
        }
        throw new LayoutParserException("invalid insets: \""+value+"\"");
    }

    public static <T extends Enum<T>> T parseEnum(Class<T> clazz, String name, boolean generalize) throws LayoutParserException {
        try {
            return Enum.valueOf(clazz, generalize ? generalizeEnumName(name) : name);
        } catch (Throwable t) {
            throw new LayoutParserException("unknown enum constant: \""+name+"\"");
        }
    }

    public static double parseDouble(String string) throws LayoutParserException {
        try {
            return Double.parseDouble(string);
        } catch (Throwable t) {
            throw new LayoutParserException("expected a number but got: \""+string+"\"");
        }
    }

    public static int parseInt(String integer) throws LayoutParserException {
        try {
            return Integer.parseInt(integer);
        } catch (Throwable t) {
            throw new LayoutParserException("expected a number but got: \""+integer+"\"");
        }
    }

    public static Class<?> classOrNull(String name) {
        try {
            return Class.forName(name);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return null;
        }
    }

    public static Object newInstanceOrNull(Class<?> type) {
        try {
            return type.newInstance();
        } catch (Throwable t) {
            t.printStackTrace();
            return null;
        }
    }

    private LayoutHelper() {}
}
