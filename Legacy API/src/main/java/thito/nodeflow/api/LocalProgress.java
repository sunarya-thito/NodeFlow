package thito.nodeflow.api;

public class LocalProgress {
    private static String activity = null;
    private static Double progress = 0d;
    public static double get() {
        return progress == null ? 0 : progress;
    }

    public static String getActivity() {
        return activity;
    }

    public static void setActivity(String activity) {
        LocalProgress.activity = activity;
    }

    public static void set(double progress) {
        LocalProgress.progress = progress;
    }
}
