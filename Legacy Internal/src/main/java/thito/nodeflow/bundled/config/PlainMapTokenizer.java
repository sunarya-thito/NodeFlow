package thito.nodeflow.bundled.config;

import java.util.*;

public class PlainMapTokenizer {
    public static Object complex(Object object) {
        Map<String, Object> complex = new HashMap<>();
        if (object == null) {
            complex.put("type", "java.lang.Object");
            complex.put("value", null);
        } else if (object instanceof String || object instanceof Number || object instanceof Boolean || object instanceof Character) {
            complex.put("type", object.getClass().getName());
            complex.put("value", object);
        } else if (object instanceof Map) {

        } else {
            complex.put("type", object.getClass().getName());

        }
        return complex;
    }
    public static Map<String, Object> complex(Map<String, Object> map) {
        Map<String, Object> complex = new HashMap<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {

        }
        return complex;
    }
    public static List<Object> complex(List<Object> list) {
        List<Object> complex = new ArrayList<>();
        for (Object e : list) {

        }
        return list;
    }
    public static Set<Object> complex(Set<Object> set) {
        Set<Object> complex = new HashSet<>();
        for (Object e : set) {

        }
        return set;
    }
}
