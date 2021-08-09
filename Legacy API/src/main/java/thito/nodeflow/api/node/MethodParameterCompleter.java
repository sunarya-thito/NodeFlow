package thito.nodeflow.api.node;

import thito.nodeflow.api.project.*;

import java.lang.reflect.*;
import java.util.*;

public interface MethodParameterCompleter {
    boolean canHandle(Method method, Parameter parameter);
    List<String> fetchValues(Project project, Method method, Parameter parameter);
}
