import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import thito.nodeflow.task.TaskThread;
import thito.nodeflow.task.batch.Batch;
import thito.nodeflow.task.batch.Progress;
import thito.nodeflow.task.thread.PoolTaskThread;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;

public class BatchTest {
    public static <T> T id(String s, T t) {
//        System.out.println(s+": "+t);
        return t;
    }
    public static void main(String[] args) {
        TaskThread THREAD = new PoolTaskThread("DEBUG");
        ArrayList<String> testList = new ArrayList<>();
        for (int i = 0; i < 10; i++) testList.add("test"+i);
        Batch.execute(THREAD, id("1", progress -> {
            System.out.println("TEST");
            progress.insert(THREAD, id("2", p -> {
                System.out.println("TEST2");
            }));
            progress.append(THREAD, id("3", p -> {
                System.out.println("TEST4");
            }));
        })).execute(THREAD, id("4", progress -> {
            System.out.println("TEST3");
            progress.insertLazy(THREAD, id("5", p -> {
                System.out.println("TEST5");
                p.proceed();
            }));
        })).execute(THREAD, id("6", progress -> {
            System.out.println("TEST6");
        })).start();
//        Batch
//            .batch(
//                Batch.execute(THREAD, progress -> {
//                    System.out.println("FIRST BATCH EXECUTION");
//                }).execute(THREAD, progress -> {
//                    System.out.println("ALTERNATIVE BATCH EXECUTION");
//                })
//            ).batch(THREAD, progress -> {
//                return Batch.execute(THREAD, p2 -> {
//                    System.out.println("SUPPLIED");
//                }).batch(Batch.execute(THREAD, progress2 -> {
//                    System.out.println("FIRST BATCH EXECUTION22");
//                }).execute(THREAD, p33 -> {
//                    System.out.println("ALTERNATIVE BATCH EXECUTION22");
//                }));
//            }).execute(THREAD, progress -> {
//                System.out.println("SECOND BATCH EXECUTION");
//            }).supply(THREAD, progress -> {
//                System.out.println("VALUE SUPPLIER");
//                return "TEST";
//            }).supply(THREAD, (progress, value) -> {
//                    System.out.println("VALUE RECEIVED: "+value);
//                    return value;
//            }).supplyBatch(THREAD, ((progress, value) -> {
//                return Batch.execute(THREAD, p -> {
//                    System.out.println("VALUE BATCH: "+value);
//                }).stream(THREAD, testList.stream(), b -> {
//                    b.execute(THREAD, (x, v) -> {
//                        System.out.println("List: "+v);
//                    });
//                });
//            })).batches(THREAD, Stream.of(
//                    Batch.execute(THREAD, p -> System.out.println("LAST")),
//                    Batch.execute(THREAD, p -> System.out.println("VERY LAST"))
//                )).executeBatch(new Progress(new SimpleStringProperty(), new SimpleDoubleProperty()));
    }
}
