package thito.nodeflow.internal.search;

public class SearchThread extends Thread {
    private static SearchThread thread;

    public static boolean shouldStop() {
        return Thread.currentThread().isInterrupted();
    }

    public static void checkThread() {
        if (Thread.currentThread() instanceof SearchThread) {
            return;
        }
        throw new IllegalStateException("not inside search thread");
    }

    public static void submit(Runnable r) {
        if (thread != null) {
            thread.interrupt();
            try {
                thread.join();
            } catch (InterruptedException e) {
                return;
            }
        }
        thread = new SearchThread(r);
        thread.start();
    }

    public SearchThread(Runnable target) {
        super(target);
    }
}
