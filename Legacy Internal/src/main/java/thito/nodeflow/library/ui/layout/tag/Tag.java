package thito.nodeflow.library.ui.layout.tag;

import java.util.*;

public class Tag {

    private List<Object> children = new ArrayList<>();
    private Map<String, String> map = new HashMap<>();

    public Map<String, String> getMap() {
        return map;
    }

    public List<Object> getChildren() {
        return children;
    }
}
