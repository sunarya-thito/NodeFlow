package thito.nodeflow.ui.docker;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.input.DataFormat;

import java.lang.ref.WeakReference;
import java.util.UUID;
import java.util.function.Supplier;

public class DockerContext {
    public static final DataFormat DOCKER_DRAG = new DataFormat("nodeflow_docker_drag");
    private UUID runtimeId = UUID.randomUUID();
    private ObjectProperty<DockerTab> drag = new SimpleObjectProperty<>();
    private Supplier<DockerWindow> dockerWindowSupplier;

    public Supplier<DockerWindow> getDockerWindowSupplier() {
        return dockerWindowSupplier;
    }

    public void setDockerWindowSupplier(Supplier<DockerWindow> dockerWindowSupplier) {
        this.dockerWindowSupplier = dockerWindowSupplier;
    }

    public UUID getRuntimeId() {
        return runtimeId;
    }

    public ObjectProperty<DockerTab> dragProperty() {
        return drag;
    }

    public DockerTab getDrag() {
        return drag.get();
    }

    public void setDrag(DockerTab drag) {
        this.drag.set(drag);
    }

}
