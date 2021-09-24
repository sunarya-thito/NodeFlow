package thito.nodeflow.debugger.client;

import thito.nodeflow.debugger.jvm.*;
import thito.nodeflow.java.*;
import thito.nodeflow.java.util.*;

import java.util.*;

public class ByteCodeHelper {
    public static void writeActivityLogger(UUID nodeId, ActivityType type) {
        Try.This(() -> {
            Java.Class(Bootstrap.class).method("reportActivity", Java.Class(String.class), Java.Class(String.class), Java.Class(ActivityType.class))
                    .invokeVoid(
                            Java.Class(Thread.class).method("currentThread").invoke()
                                .method("getName").invoke(),
                            nodeId.toString(),
                            type);
        }).Catch(Java.Class(Throwable.class), e -> {});
    }
}
