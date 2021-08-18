package thito.nodeflow.library.ui.handler;

import javafx.application.*;
import javafx.event.*;
import javafx.scene.control.*;
import org.jsoup.nodes.*;
import thito.nodeflow.library.ui.*;

import java.util.*;

public class ToggleButtonSkinHandler implements SkinHandler<ToggleButton> {
    private static Map<String, ToggleGroup> map = new HashMap<>();
    private static Map<String, ToggleGroup> persistentMap = new HashMap<>();
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
