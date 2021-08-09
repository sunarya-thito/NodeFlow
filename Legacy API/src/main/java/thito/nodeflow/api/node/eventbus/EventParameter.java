package thito.nodeflow.api.node.eventbus;

public interface EventParameter {
    static EventParameter create(String name, Class<?> type) {
        return new EventParameter() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public Class<?> getType() {
                return type;
            }
        };
    }
    String getName();
    Class<?> getType();
}
