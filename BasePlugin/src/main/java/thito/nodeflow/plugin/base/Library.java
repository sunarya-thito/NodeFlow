package thito.nodeflow.plugin.base;

import thito.nodeflow.plugin.base.blueprint.node.Function;

import java.io.File;
import java.util.List;

public interface Library {
    String getHashCode();
    File getFile();
    List<Function> getFunctionList();
}
