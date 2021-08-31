package thito.nodeflow.library.util;

import javafx.beans.binding.*;
import javafx.geometry.*;
import javafx.scene.paint.*;
import javafx.scene.robot.*;
import thito.nodeflow.library.language.*;

import java.net.*;
import java.nio.charset.*;
import java.util.*;
import java.util.stream.*;

import static java.util.stream.Collectors.*;

public class Toolkit {
    private static Robot robot;
    public static final double SIZE_BYTE = 1;
    public static final double SIZE_KILO_BYTE = SIZE_BYTE * 1024;
    public static final double SIZE_MEGA_BYTE = SIZE_KILO_BYTE * 1024;
    public static final double SIZE_GIGA_BYTE = SIZE_MEGA_BYTE * 1024;
    public static final long TIME_MILLISECONDS = 1;
    public static final long TIME_SECONDS = TIME_MILLISECONDS * 1000;
    public static final long TIME_MINUTES = TIME_SECONDS * 60;
    public static final long TIME_HOURS = TIME_MINUTES * 60;
    public static final long TIME_DAYS = TIME_HOURS * 24;
    public static final long TIME_MONTHS = TIME_DAYS * 30;

    static {
        try {
            robot = new Robot();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static Point2D mouse() {
        return robot.getMousePosition();
    }

    public static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }

    /**
     * Read the list of all bundled java classes
     * @return
     */
    public static List<String> readAllJavaClasses() {
        ArrayList<String> classes = new ArrayList<>();
        // TODO
        return classes;
    }

    public static StringBinding timeSinceFormat(NumberExpression timeDelta) {
        return Bindings.createStringBinding(() -> {
            long time = timeDelta.longValue();
            if (time >= TIME_MONTHS) {
                return String.format(I18n.$("time-since.monthly").get(), time / TIME_MONTHS);
            }
            if (time >= TIME_DAYS * 2) {
                return String.format(I18n.$("time-since.daily").get(), time / TIME_DAYS);
            }
            if (time >= TIME_DAYS) {
                return I18n.$("time-since.yesterday").get();
            }
            if (time >= TIME_HOURS) {
                return String.format(I18n.$("time-since.hourly").get(), time / TIME_HOURS);
            }
            if (time >= TIME_MINUTES) {
                return String.format(I18n.$("time-since.monthly").get(), time / TIME_MINUTES);
            }
            return I18n.$("time-since.secondly").format(time / TIME_SECONDS).get();
        }, timeDelta, I18n.$("time-since.monthly"), I18n.$("time-since.daily"), I18n.$("time-since.yesterday"),
                I18n.$("time-since.hourly"), I18n.$("time-since.minutely"), I18n.$("time-since.secondly"));
    }

    public static StringBinding formatFileSize(NumberExpression bytes) {
        return Bindings.createStringBinding(() -> {
            double size = bytes.doubleValue();
            if (size >= SIZE_GIGA_BYTE) {
                return String.format(I18n.$("file-size.gB").get(), size / SIZE_GIGA_BYTE);
            }
            if (size >= SIZE_MEGA_BYTE) {
                return String.format(I18n.$("file-size.mB").get(), size / SIZE_MEGA_BYTE);
            }
            if (size >= SIZE_KILO_BYTE) {
                return String.format(I18n.$("file-size.kB").get(), size / SIZE_KILO_BYTE);
            }
            return String.format(I18n.$("file-size.B").get(), size / SIZE_BYTE);
        }, bytes, I18n.$("file-size.gB"), I18n.$("file-size.mB"), I18n.$("file-size.kB"), I18n.$("file-size.B"));
    }

    public static int colorToRGBInt(Color color) {
        int a = (int) (color.getOpacity() * 255);
        int r = (int) (color.getRed() * 255);
        int g = (int) (color.getGreen() * 255);
        int b = (int) (color.getBlue() * 255);
        return ((a & 0xFF) << 24) |
                ((r & 0xFF) << 16) |
                ((g & 0xFF) << 8)  |
                (b & 0xFF);
    }

    public static Map<String, List<String>> splitQuery(URL url) {
        if (isNullOrEmpty(url.getQuery())) {
            return Collections.emptyMap();
        }
        return Arrays.stream(url.getQuery().split("&"))
                .map(Toolkit::splitQueryParameter)
                .collect(Collectors.groupingBy(AbstractMap.SimpleImmutableEntry::getKey, LinkedHashMap::new, mapping(Map.Entry::getValue, Collectors.toList())));
    }

    public static AbstractMap.SimpleImmutableEntry<String, String> splitQueryParameter(String it) {
        final int idx = it.indexOf("=");
        final String key = idx > 0 ? it.substring(0, idx) : it;
        final String value = idx > 0 && it.length() > idx + 1 ? it.substring(idx + 1) : null;
        return new AbstractMap.SimpleImmutableEntry<>(
                URLDecoder.decode(key, StandardCharsets.UTF_8),
                URLDecoder.decode(value, StandardCharsets.UTF_8)
        );
    }

    public static double searchScore(String target, String search) {
        String[] split = search.split("\\s+");
        String ignoreCaseTarget = target.toLowerCase();
        double score = 0;
        double maxScore = target.split("\\s+").length * 2;
        int previousIndex = -1;
        int ignoreCasePreviousIndex = -1;
        for (int i = 0; i < split.length; i++) {
            int index = target.indexOf(split[i]);
            if (previousIndex < index) {
                score += 2;
                previousIndex = index;
                continue;
            }
            String ignoreCaseSearch = split[i].toLowerCase();
            int ignoreCaseIndex = ignoreCaseTarget.indexOf(ignoreCaseSearch);
            if (ignoreCasePreviousIndex < ignoreCaseIndex) {
                score++;
                ignoreCasePreviousIndex = ignoreCaseIndex;
            }
        }
        return score / maxScore;
    }

    public static double distance(String s1, String s2) {
        String longer = s1, shorter = s2;
        if (s1.length() < s2.length()) { // longer should always have greater length
            longer = s2; shorter = s1;
        }
        int longerLength = longer.length();
        if (longerLength == 0) { return 1.0; /* both strings are zero length */ }
        return (longerLength - editDistance(longer, shorter)) / (double) longerLength;
    }

    // Example implementation of the Levenshtein Edit Distance
    // See http://rosettacode.org/wiki/Levenshtein_distance#Java
    public static int editDistance(String s1, String s2) {
        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();

        int[] costs = new int[s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) {
            int lastValue = i;
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0)
                    costs[j] = j;
                else {
                    if (j > 0) {
                        int newValue = costs[j - 1];
                        if (s1.charAt(i - 1) != s2.charAt(j - 1))
                            newValue = Math.min(Math.min(newValue, lastValue),
                                    costs[j]) + 1;
                        costs[j - 1] = lastValue;
                        lastValue = newValue;
                    }
                }
            }
            if (i > 0)
                costs[s2.length()] = lastValue;
        }
        return costs[s2.length()];
    }

}
