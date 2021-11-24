package thito.nodeflow.ui.handler;

import javafx.scene.layout.*;
import org.jsoup.nodes.*;
import thito.nodeflow.ui.SkinHandler;
import thito.nodeflow.ui.SkinParser;

public class FlowPaneSkinHandler implements SkinHandler<FlowPane> {
    @Override
    public void parse(SkinParser parser, FlowPane node, Element element) {
        if (element.hasAttr("flowpane.prefwraplength")) {
            node.setPrefWrapLength(Double.parseDouble(element.attr("flowpane.prefwraplength")));
        }
    }
}
