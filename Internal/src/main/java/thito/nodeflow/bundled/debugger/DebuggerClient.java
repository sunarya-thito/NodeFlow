package thito.nodeflow.bundled.debugger;

import thito.nodeflow.api.*;
import thito.nodeflow.api.editor.*;
import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.api.project.*;
import thito.nodeflow.api.task.*;
import thito.nodeflow.internal.editor.*;
import thito.nodeflow.internal.node.*;
import thito.nodeflow.internal.project.*;

import java.io.*;
import java.net.*;
import java.util.*;

public class DebuggerClient {
    private Socket socket;

    public DebuggerClient() {
    }

    public void connect(String host, int port) throws IOException {
        Socket socket = new Socket(host, port);
        DataInputStream inputStream = new DataInputStream(socket.getInputStream());
        while (socket.isConnected()) {
            long sourceMost = inputStream.readLong();
            long sourceLeast = inputStream.readLong();
            long targetMost = inputStream.readLong();
            long targetLeast = inputStream.readLong();
            int action = inputStream.readInt();
            UUID source = new UUID(sourceMost, sourceLeast);
            UUID target = new UUID(targetMost, targetLeast);
            handleBroadcastAction(source, target, action);
        }
    }

    public void handleBroadcastAction(UUID source, UUID target, int action) {
        Task.runOnBackground("action-received", () -> {
            for (Project loaded : NodeFlow.getApplication().getProjectManager().getLoadedProjects()) {
                for (ProjectTab tab : loaded.getEditorWindow().getOpenedFiles()) {
                    FileSession session = ((ProjectTabImpl) tab).sessionProperty().get();
                    if (session instanceof NodeFileSession) {
                        StandardNodeModule module = ((NodeFileSession) session).getModule();
                        for (Link link : new HashSet<>(module.getLinks())) {
                            if (link.getSourceId().equals(source) && link.getTargetId().equals(target)) {

                                return;
                            }
                        }
                    }
                }
            }
        });
    }
}
