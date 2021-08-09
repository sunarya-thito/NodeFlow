package thito.nodeflow.api;

public interface Version extends Comparable<Version> {
    State getState();

    int getMajor();

    int getMinor();

    int getBuild();

    enum State {
        ALPHA("A", "Alpha"), BETA("B", "Beta"), RELEASE("R", "Release");
        private static final State[] CACHE = values();
        private final String alias, properName;

        State(String alias, String properName) {
            this.alias = alias;
            this.properName = properName;
        }

        public static State getState(String name) {
            for (State state : CACHE) {
                if (state.name().equalsIgnoreCase(name) || state.getAlias().equalsIgnoreCase(name)) {
                    return state;
                }
            }
            return null;
        }

        public String getAlias() {
            return alias;
        }

        public String getProperName() {
            return properName;
        }
    }
}
