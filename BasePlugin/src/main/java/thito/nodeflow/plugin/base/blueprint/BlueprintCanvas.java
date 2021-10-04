package thito.nodeflow.plugin.base.blueprint;

import thito.nodeflow.engine.node.NodeCanvas;
import thito.nodeflow.engine.node.state.NodeCanvasState;

import java.io.*;
import java.util.UUID;

public class BlueprintCanvas {
    private BlueprintHeader header;
    private NodeCanvas canvas = new NodeCanvas(new BlueprintHandler(BlueprintManager.getManager().createRegistry(), this));
    private UUID id;

    public void load(InputStream inputStream) throws IOException, ClassNotFoundException {
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
        canvas.loadState((NodeCanvasState) objectInputStream.readObject());
    }

    public void save(OutputStream outputStream) throws IOException {
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        NodeCanvasState state = canvas.saveState();
        objectOutputStream.writeObject(state);
    }

    public NodeCanvas getCanvas() {
        return canvas;
    }
}
