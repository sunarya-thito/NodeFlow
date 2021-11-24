public class ThreadTest {
    static Object lock = new Object();
    public static void main(String[] args) throws Throwable {
        Thread thread = new Thread(() -> {
            synchronized (ThreadTest.class) {
                while (lock.equals(lock));
            }
            System.out.println("stopped");
        });
        thread.start();
        Thread.sleep(100);
//        thread.stop();
        synchronized (ThreadTest.class) {
            System.out.println("test: "+Thread.holdsLock(ThreadTest.class));
        }
    }
}
