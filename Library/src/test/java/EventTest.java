import javafx.concurrent.*;
import javafx.event.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.media.*;
import javafx.scene.transform.*;
import javafx.scene.web.*;
import javafx.stage.*;

import java.lang.reflect.*;
import java.util.*;

public class EventTest {
    public static void main(String[] args) {
        Class events[] = {
                Event.class,
                MouseEvent.class,
                ActionEvent.class,
                CheckBoxTreeItem.TreeModificationEvent.class,
                DialogEvent.class,
                InputEvent.class,
                ListView.EditEvent.class,
                MediaErrorEvent.class,
                ScrollToEvent.class,
                SortEvent.class,
                TableColumn.CellEditEvent.class,
                TransformChangedEvent.class,
                TreeItem.TreeModificationEvent.class,
                TreeTableColumn.CellEditEvent.class,
                TreeTableView.EditEvent.class,
                TreeView.EditEvent.class,
                WebErrorEvent.class,
                WebEvent.class,
                WindowEvent.class,
                WorkerStateEvent.class
        };
        Set<String> names = new LinkedHashSet<>();
        for (Class c : events) {
            for (Field f : c.getDeclaredFields()) {
                if (f.getType() == EventType.class && Modifier.isPublic(f.getModifiers())) {
                    String name = f.getName();
                    if (names.contains(name)) {
                        String className = c.getCanonicalName();
                        className = className.replace(c.getPackageName()+".", "").replace(".", "_");
                        name = className.toUpperCase()+"_"+name;
                    }
                    if (name.equals("eventType")) {
                        System.out.println(f);
                    }
                    names.add(name);
                    System.out.println(name+"("+c.getCanonicalName()+"."+f.getName()+"), ");
                }
            }
        }
    }

}
