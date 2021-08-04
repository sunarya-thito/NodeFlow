package thito.nodeflow.api.project;

import thito.nodeflow.api.task.*;

import java.io.*;

public interface ProjectDebugger {

    OutputStream getOutputStream();
    void submit(Runnable execution);
    void terminate();

}
