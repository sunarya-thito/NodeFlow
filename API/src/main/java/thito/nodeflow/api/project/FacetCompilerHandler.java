package thito.nodeflow.api.project;

import thito.nodeflow.api.node.*;
import thito.nodeflow.api.node.eventbus.*;
import thito.nodeflow.api.node.eventbus.command.*;
import thito.nodeflow.api.resource.*;
import thito.reflectedbytecode.*;

import java.io.*;
import java.util.*;
import java.util.jar.*;

public interface FacetCompilerHandler {
    Project getProject();
    Set<NodeModule> getModules();
    List<CompiledEvent> getEventMethods(EventProvider provider);
    List<CompiledEvent> getEventMethods(String providerId);
    List<CompiledCommandNode> getCommandMethods(CommandNodeCategory category);
    Context getCompilerContext();
    void bundleResource(String name, InputStream inputStream);
    void bundleResource(String name, byte[] bytes);
    void bundleResource(String name, JarFile file);
    void bundleResource(String name, Resource resource);
    void configLoad(Reference configDirectory);
    void configSave(Reference configDirectory);
    Properties getManifest();
    String getPackageName();
}
