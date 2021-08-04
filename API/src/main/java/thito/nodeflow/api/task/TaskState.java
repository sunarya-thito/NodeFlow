package thito.nodeflow.api.task;

public enum TaskState {
    /**
     * The task is currently running
     */
    RUNNING,
    /**
     * The task is currently idling, not inside a run queue, and not yet running
     */
    IDLE,
    /**
     * The task is done
     */
    DONE
}
