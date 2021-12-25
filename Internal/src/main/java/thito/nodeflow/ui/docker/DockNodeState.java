package thito.nodeflow.ui.docker;

import java.io.Serializable;

public interface DockNodeState extends Serializable {
    static boolean check(DockNodeState state, Class<? extends DockNodeState> type) {
        return state == null || type.isInstance(state);
    }
}
