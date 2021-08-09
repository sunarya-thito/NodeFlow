package thito.nodeflow.internal.node.yml;

import thito.nodeflow.api.node.*;
import thito.nodeflow.api.project.*;
import thito.nodeflow.internal.project.*;

import java.lang.reflect.*;
import java.util.*;

public class YamlCompleter implements MethodParameterCompleter {

    private static List<String> handledClasses = Arrays.asList(
            "org.bukkit.configuration.MemorySection",
            "org.bukkit.configuration.ConfigurationSection"
    );
    private static List<String> handledMethods = Arrays.asList(
            "get",
            "getBoolean",
            "getBooleanList",
            "getByteList",
            "getCharacterList",
            "getColor",
            "getConfigurationSection",
            "getDouble",
            "getDoubleList",
            "getFloatList",
            "getInt",
            "getIntegerList",
            "getItemStack",
            "getList",
            "getLocation",
            "getLong",
            "getLongList",
            "getMapList",
            "getObject",
            "getOfflinePlayer",
            "getSerializable",
            "getShortList",
            "getString",
            "getStringList",
            "getVector",
            "isBoolean",
            "isColor",
            "isConfigurationSection",
            "isDouble",
            "isInt",
            "isItemStack",
            "isList",
            "isLocation",
            "isLong",
            "isOfflinePlayer",
            "isSet",
            "isString",
            "isVector",
            "set",
            "contains",
            "addDefault"
    );

    @Override
    public boolean canHandle(Method method, Parameter parameter) {
        if (handledClasses.contains(method.getDeclaringClass().getName())) {
            if (handledMethods.contains(method.getName())) {
                int stringIndex = Arrays.asList(method.getParameterTypes()).indexOf(String.class);
                int index = Arrays.asList(method.getParameters()).indexOf(parameter);
                if (stringIndex == index) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public List<String> fetchValues(Project project, Method method, Parameter parameter) {
        return new ArrayList<>(((ProjectImpl) project).getYamlPaths());
    }

}
