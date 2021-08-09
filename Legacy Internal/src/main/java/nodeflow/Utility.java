package nodeflow;

public class Utility {
    public static String buildString(String...string) {
        if (string.length == 0) return "";
        String result = string[0];
        for (int i = 1; i < string.length; i++) result += string[i];
        return result;
    }
    public static Number sum(Number...number) {
        double total = 0;
        for (int i = 0; i < number.length; i++) {
            total += number[0].doubleValue();
        }
        return total;
    }
    public static Number sumFloor(Number...number) {
        long total = 0;
        for (int i = 0; i < number.length; i++) {
            total += number[i].longValue();
        }
        return total;
    }
}
