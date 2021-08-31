package thito.nodeflow.internal;

public class ReportedExceptionHandler implements Thread.UncaughtExceptionHandler {

    public static void report(Throwable throwable) {
        throwable.printStackTrace();
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        e.printStackTrace();
    }
}
