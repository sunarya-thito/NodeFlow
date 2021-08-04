package thito.nodeflow.api.editor.node;

import thito.reflectedbytecode.*;

import java.util.*;

public interface NodeCompileHandler {
    GClass getCompiledClass();
    NodeCompileSession getCompileSession(Node node);
    Reference getReference(NodeParameter input);
    Reference getParameterReference(int index);
    void returnValue(Object object);
    void putReference(NodeParameter parameter, Object reference);
    ILocalField createLocalVariable(IClass type);
    Set<NodeParameter> getRequiredParameters(Node node);
    void compile(Node node);
    void doCleanUp();
}
