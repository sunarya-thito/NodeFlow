package thito.nodeflow.internal.editor;

import thito.nodeflow.api.editor.*;
import thito.nodeflow.api.node.*;
import thito.nodeflow.bundled.editor.*;
import thito.nodeflow.internal.editor.config.*;
import thito.nodeflow.internal.editor.record.*;
import thito.nodeflow.internal.node.yml.*;

import java.lang.reflect.*;
import java.util.*;

public class EditorManagerImpl implements EditorManager {
    private final Map<String, FileHandler> editorMap = new HashMap<>();
    private final Set<MethodParameterCompleter> completers = new HashSet<>();

    public EditorManagerImpl() {
        registerHandler(new NodeFileHandler());
        registerHandler(new ConfigFileHandler());
        registerHandler(new RecordFileHandler());
        registerHandler(new YamlFileHandler());
        registerCompleter(new YamlCompleter());
    }

    @Override
    public void registerCompleter(MethodParameterCompleter completer) {
        completers.add(completer);
    }

    @Override
    public void unregisterCompleter(MethodParameterCompleter completer) {
        completers.remove(completer);
    }

    public MethodParameterCompleter getHandler(Method method, Parameter parameter) {
        for (MethodParameterCompleter completer : completers) {
            if (completer.canHandle(method, parameter)) {
                return completer;
            }
        }
        return null;
    }

    @Override
    public FileHandler getRegisteredHandler(String fileExtension) {
        return editorMap.get(fileExtension);
    }

    @Override
    public void registerHandler(FileHandler editor) {
        editorMap.put(editor.getExtension(), editor);
    }

    @Override
    public void unregisterHandler(FileHandler editor) {
        editorMap.remove(editor.getExtension());
    }

    @Override
    public boolean canHandle(String fileExtension) {
        return editorMap.containsKey(fileExtension);
    }

    @Override
    public Collection<FileHandler> getEditors() {
        return editorMap.values();
    }
}
