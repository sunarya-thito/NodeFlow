package thito.nodeflow.api.editor.node;

import java.util.*;

public interface Link {
    UUID getSourceId();
    UUID getTargetId();
    NodeParameter getSource();
    NodeParameter getTarget();
}
