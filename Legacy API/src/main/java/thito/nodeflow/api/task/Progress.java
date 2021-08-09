package thito.nodeflow.api.task;

public interface Progress {
    static void progress(Type type, Object source) {
        progress(type, source, 0, 1);
    }

    static void progress(Type type, Object source, int current, int max) {
        DynamicTask.progress(current, max);
        DynamicTask.PROCESS.get().set(new Progress() {
            @Override
            public Type getType() {
                return type;
            }

            @Override
            public Object getSource() {
                return source;
            }
        });
    }

    Type getType();

    Object getSource();

    enum Type {
        LOADING_FILE, UNLOADING_FILE,
        LOADING_INTERFACE
    }
}
