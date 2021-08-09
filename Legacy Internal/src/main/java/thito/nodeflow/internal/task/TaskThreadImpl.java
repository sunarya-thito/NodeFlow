package thito.nodeflow.internal.task;

import thito.nodeflow.api.task.*;

import java.util.*;
import java.util.concurrent.*;

public class TaskThreadImpl implements TaskThread {

    private final List<Task> tasks = new ArrayList<>();

    private final ScheduledExecutorService threadPool;

    public TaskThreadImpl(String name) {
        threadPool = Executors.newSingleThreadScheduledExecutor(run -> new Thread(run, name));
    }

    public TaskThreadImpl(String name, int amount) {
        threadPool = Executors.newScheduledThreadPool(amount, run -> new Thread(run, name));
    }

    @Override
    public String getName() {
        return "Background";
    }

    public void shutdown() {
        try {
            threadPool.shutdown();
            threadPool.awaitTermination(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Task> getActiveTasks() {
        return tasks;
    }

    @Override
    public Task runTask(Task task) {
        tasks.add(task);
        threadPool.submit(() -> {
            if (task instanceof AbstractTaskImpl) {
                ((AbstractTaskImpl) task).setThread(this);
                ((AbstractTaskImpl) task).setState(TaskState.RUNNING);
            }
            try {
                if (task instanceof AbstractTaskImpl) {
                    ((AbstractTaskImpl) task).run();
                }
            } catch (Throwable t) {
                // TODO actually handle the ReportedError
                t.printStackTrace();
            }
            tasks.remove(task);
        });
        return task;
    }

    @Override
    public Task runTaskRepeatedly(Task task, Duration delay, Duration period) {
        tasks.add(task);
        threadPool.scheduleAtFixedRate(() -> {
            if (task instanceof AbstractTaskImpl) {
                ((AbstractTaskImpl) task).setState(TaskState.RUNNING);
            }
            try {
                if (task instanceof AbstractTaskImpl) {
                    ((AbstractTaskImpl) task).run();
                }
            } catch (Throwable t) {
                // TODO actually handle the ReportedError
                t.printStackTrace();
            }
            if (task instanceof AbstractTaskImpl) {
                ((AbstractTaskImpl) task).setState(TaskState.IDLE);
            }
            tasks.remove(task);
        }, delay.asMillis(), period.asMillis(), TimeUnit.MILLISECONDS);
        return task;
    }

    @Override
    public Task runTaskLater(Task task, Duration delay) {
        tasks.add(task);
        threadPool.schedule(() -> {
            if (task instanceof AbstractTaskImpl) {
                ((AbstractTaskImpl) task).setState(TaskState.RUNNING);
            }
            try {
                if (task instanceof AbstractTaskImpl) {
                    ((AbstractTaskImpl) task).run();
                }
            } catch (Throwable t) {
                // TODO actually handle the ReportedError
                t.printStackTrace();
            }
            if (task instanceof AbstractTaskImpl) {
                ((AbstractTaskImpl) task).setState(TaskState.IDLE);
            }
            tasks.remove(task);
        }, delay.asMillis(), TimeUnit.MILLISECONDS);
        return task;
    }

}
