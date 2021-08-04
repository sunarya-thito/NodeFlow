package thito.nodeflow.internal.ui;

import javafx.beans.Observable;
import javafx.collections.*;
import javafx.stage.*;
import thito.nodeflow.api.ui.Window;

import java.util.*;
import java.util.concurrent.*;

public class StaticMenu {
    private Stage owner;
    private ListChangeListener<Screen> screenListChangeListener;

    private Map<Screen, StaticMenuBar> bars = new ConcurrentHashMap<>();

    private ObservableList<Screen> screens = FXCollections.observableArrayList();

    public StaticMenu(Stage owner) {
        this.owner = owner;
        screenListChangeListener = c -> {
            while (c.next()) {
                for (Screen added : c.getAddedSubList()) {
                    if (!bars.containsKey(added)) {
                        StaticMenuBar bar = new StaticMenuBar(added, owner);
                        bar.show();
                        bars.put(added, bar);
                    }
                }
                for (Screen removed : c.getRemoved()) {
                    StaticMenuBar bar = bars.remove(removed);
                    if (bar != null) {
                        bar.destroy();
                    }
                }
            }
        };
        initListener();
    }

    private void updateScreen(Observable obs) {
        List<Screen> screenList = Screen.getScreensForRectangle(owner.getX() + 10, owner.getY() + 10, owner.getWidth() - 10, owner.getHeight() - 10);
        screens.retainAll(screenList);
        screenList.removeAll(screens);
        screens.addAll(screenList);
    }

    private void initListener() {
        owner.xProperty().addListener(this::updateScreen);
        owner.yProperty().addListener(this::updateScreen);
        owner.maximizedProperty().addListener(this::updateScreen);
        owner.iconifiedProperty().addListener(this::updateScreen);
        owner.heightProperty().addListener(this::updateScreen);
        owner.widthProperty().addListener(this::updateScreen);
        screens.addListener(screenListChangeListener);
        for (Screen screen : screens) {
            if (!bars.containsKey(screen)) {
                StaticMenuBar bar = new StaticMenuBar(screen, owner);
                bar.show();
                bars.put(screen, bar);
            }
        }
    }

    public void toFront() {
        for (StaticMenuBar bar : bars.values()) {
            bar.toFront();
        }
    }

    public void toBack() {
        for (StaticMenuBar bar : bars.values()) {
            bar.toBack();
        }
    }

    public void destroy() {
        screens.removeListener(screenListChangeListener);
        for (StaticMenuBar bar : bars.values()) {
            bar.destroy();
        }
    }
}
