package thito.nodeflow.library.ui.animation;

import javafx.collections.*;
import javafx.scene.*;

import java.util.*;

public interface ChildrenChange {
    Map<String, ChildrenChange> REGISTERED = new HashMap<String, ChildrenChange>() {
        {
            put("FadeInChildrenChange", new FadeInChildrenChange());
        }
    };
    static void hook(Parent parent, ChildrenChange change) {
        ListChangeListener old = (ListChangeListener) parent.getProperties().get(ChildrenChange.class);
        if (old != null) {
            parent.getChildrenUnmodifiable().removeListener(old);
        }
        old = cx -> {
            change.refresh(parent, cx.getAddedSubList(), cx.getRemoved());
        };
        parent.getProperties().put(ChildrenChange.class, old);
        parent.getChildrenUnmodifiable().addListener(old);
    }
    void refresh(Parent parent, List<Node> added, List<Node> removed);
}
