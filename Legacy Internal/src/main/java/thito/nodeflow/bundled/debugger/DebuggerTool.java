package thito.nodeflow.bundled.debugger;

import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.internal.node.*;
import thito.reflectedbytecode.jvm.*;

import java.util.*;

public class DebuggerTool {
    public static void compileReport(NodeCompileHandler handler, NodeParameter previous, NodeParameter next, Runnable scope) {
        NodeCompileHandlerImpl nat = (NodeCompileHandlerImpl) handler;
        Set<Link> linked = new HashSet<>();
        for (Link link : previous.getNode().getModule().getLinks()) {
            if (link.getSource() == previous && link.getTarget() == next) {
                linked.add(link);
                Java.Class(DebuggerServer.class).getMethod("report", UUID.class, UUID.class, int.class).get()
                        .invoke(null, link.getSourceId(), link.getTarget(), 1);
            }
        }
        scope.run();
        for (Link link : linked) {
            Java.Class(DebuggerServer.class).getMethod("report", long.class, long.class, long.class, long.class, int.class).get()
                    .invoke(null,
                            link.getSourceId().getMostSignificantBits(),
                            link.getSourceId().getLeastSignificantBits(),
                            link.getTargetId().getMostSignificantBits(),
                            link.getTargetId().getLeastSignificantBits(), 0);
        }
    }
}
