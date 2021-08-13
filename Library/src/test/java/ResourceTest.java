import javafx.collections.*;
import thito.nodeflow.library.resource.*;

import java.io.*;

public class ResourceTest {

    private static ListChangeListener<Resource> listChangeListener;

    public static void main(String[] args) {
        ResourceManager manager = new ResourceManager(new File("D:/Countdown UTBK/"));
        Resource resource = manager.getRoot();
        listChangeListener = change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    change.getAddedSubList().forEach(ResourceTest::addListener);
                    System.out.println("added: "+change.getAddedSubList());
                }
                if (change.wasRemoved()) {
                    change.getRemoved().forEach(ResourceTest::removeListener);
                    System.out.println("removed "+change.getRemoved());
                }
            }
        };
        addListener(resource);
    }

    static void removeListener(Resource resource) {
        resource.getChildren().removeListener(listChangeListener);
    }

    static void addListener(Resource resource) {
        resource.getChildren().addListener(listChangeListener);
        resource.addEventHandler(ResourceEvent.FILE_MODIFIED, event -> {
            System.out.println("File change: "+event.getResource().getSize());
        });
        for (Resource child : resource.getChildren()) {
            addListener(child);
        }
    }
}
