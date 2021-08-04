package thito.nodeflow.library;

import java.util.*;

public class ExtraList<T> extends ArrayList<T> {

    public ExtraList(List<T> list, T... extras) {
        super(list.size() + extras.length);
        addAll(list);
        for (T extra : extras) add(extra);
    }

}
