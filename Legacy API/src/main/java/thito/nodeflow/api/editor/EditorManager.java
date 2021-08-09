package thito.nodeflow.api.editor;

import thito.nodeflow.api.node.*;

import java.util.*;

public interface EditorManager {
    void registerCompleter(MethodParameterCompleter completer);

    void unregisterCompleter(MethodParameterCompleter completer);

    FileHandler getRegisteredHandler(String fileExtension);

    Collection<FileHandler> getEditors();

    void registerHandler(FileHandler editor);

    void unregisterHandler(FileHandler editor);

    boolean canHandle(String fileExtension);
}
