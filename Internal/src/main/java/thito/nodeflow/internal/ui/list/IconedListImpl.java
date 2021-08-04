package thito.nodeflow.internal.ui.list;

import thito.nodeflow.api.ui.list.*;

import java.util.*;

public class IconedListImpl implements IconedList {
    private Map<Class<?>, IconedListHandler> handlerMap = new HashMap<>();
    @Override
    public void register(Class<?> type, IconedListHandler handler) {
        handlerMap.put(type, handler);
    }

    public IconedListHandler get(Class<?> type) {
        return handlerMap.get(type);
    }
}
