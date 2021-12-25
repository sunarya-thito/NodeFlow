package thito.nodeflow.ui.docker;

import java.io.Serializable;
import java.util.UUID;

public class DockerDrag implements Serializable {
    private static final long serialVersionUID = 1L;

    public final UUID contextId;

    public DockerDrag(UUID contextId) {
        this.contextId = contextId;
    }
}
