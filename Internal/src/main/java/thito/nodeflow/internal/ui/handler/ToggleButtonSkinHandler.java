package thito.nodeflow.internal.ui.handler;

import javafx.application.*;
import javafx.event.*;
import javafx.scene.control.*;
import org.jsoup.nodes.*;
import thito.nodeflow.internal.ui.*;

import java.util.*;

public class ToggleButtonSkinHandler implements SkinHandler<ToggleButton> {
    private static Map<String, ToggleGroup> map = new HashMap<>();
    public static ToggleGroup getGroup(String name) {
        return map.computeIfAbsent(name, n -> new ToggleGroup());
    }
    @Override
    public void parse(SkinParser parser, ToggleButton node, Element element) {
        if (element.hasAttr("togglebutton.group")) {
            node.setToggleGroup(map.computeIfAbsent(element.attr("togglebutton.group"), name -> new ToggleGroup()));
        }
        if (element.hasAttr("selected")) {
            node.setSelected(true);
            Platform.runLater(() -> {
                node.fireEvent(new ActionEvent());
            });
        }
    }
}
