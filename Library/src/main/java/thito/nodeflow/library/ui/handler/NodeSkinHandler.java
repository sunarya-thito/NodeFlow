package thito.nodeflow.library.ui.handler;

import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.*;
import org.jsoup.nodes.*;
import thito.nodeflow.library.ui.*;

public class NodeSkinHandler implements SkinHandler<Node> {
    @Override
    public void parse(SkinParser parser, Node node, Element element) {
        if (element.hasAttr("id")) {
            node.setId(element.id());
        }
        if (element.hasAttr("class")) {
            node.getStyleClass().addAll(element.attr("class").split("\\s+"));
        }
        if (element.hasAttr("style")) {
            node.setStyle(element.attr("style"));
        }
        if (element.hasAttr("pickonbounds")) {
            node.setPickOnBounds(Boolean.parseBoolean(element.attr("pickonbounds")));
        }
        if (element.hasAttr("layoutx")) {
            node.setLayoutX(Double.parseDouble(element.attr("layoutx")));
        }
        if (element.hasAttr("layouty")) {
            node.setLayoutY(Double.parseDouble(element.attr("layouty")));
        }
        if (element.hasAttr("managed")) {
            node.setManaged(Boolean.parseBoolean(element.attr("managed")));
        }
        if (element.hasAttr("anchorpane.top")) {
            AnchorPane.setTopAnchor(node, Double.parseDouble(element.attr("anchorpane.top")));
        }
        if (element.hasAttr("anchorpane.bottom")) {
            AnchorPane.setBottomAnchor(node, Double.parseDouble(element.attr("anchorpane.bottom")));
        }
        if (element.hasAttr("anchorpane.left")) {
            AnchorPane.setLeftAnchor(node, Double.parseDouble(element.attr("anchorpane.left")));
        }
        if (element.hasAttr("anchorpane.right")) {
            AnchorPane.setRightAnchor(node, Double.parseDouble(element.attr("anchorpane.right")));
        }
        if (element.hasAttr("splitpane.resizewithparent")) {
            SplitPane.setResizableWithParent(node, Boolean.parseBoolean(element.attr("splitpane.resizewithparent")));
        }
        if (element.hasAttr("borderpane.alignment")) {
            BorderPane.setAlignment(node, Pos.valueOf(element.attr("borderpane.alignment")));
        }
        if (element.hasAttr("borderpane.margin")) {
            String[] split = element.attr("borderpane.margin").split(" ");
            if (split.length == 1) {
                BorderPane.setMargin(node, new Insets(Double.parseDouble(split[0])));
            } else if (split.length == 2) {
                BorderPane.setMargin(node, new Insets(Double.parseDouble(split[0]), Double.parseDouble(split[1]),
                        Double.parseDouble(split[0]), Double.parseDouble(split[1])));
            } else if (split.length == 4) {
                BorderPane.setMargin(node, new Insets(Double.parseDouble(split[0]), Double.parseDouble(split[1]),
                        Double.parseDouble(split[2]), Double.parseDouble(split[3])));
            }
        }
        if (element.hasAttr("vbox.grow")) {
            VBox.setVgrow(node, Priority.valueOf(element.attr("vbox.grow")));
        }
        if (element.hasAttr("vbox.margin")) {
            String[] split = element.attr("vbox.margin").split(" ");
            if (split.length == 1) {
                VBox.setMargin(node, new Insets(Double.parseDouble(split[0])));
            } else if (split.length == 2) {
                VBox.setMargin(node, new Insets(Double.parseDouble(split[0]), Double.parseDouble(split[1]),
                        Double.parseDouble(split[0]), Double.parseDouble(split[1])));
            } else if (split.length == 4) {
                VBox.setMargin(node, new Insets(Double.parseDouble(split[0]), Double.parseDouble(split[1]),
                        Double.parseDouble(split[2]), Double.parseDouble(split[3])));
            }
        }
        if (element.hasAttr("hbox.grow")) {
            HBox.setHgrow(node, Priority.valueOf(element.attr("hbox.grow")));
        }
        if (element.hasAttr("hbox.margin")) {
            String[] split = element.attr("hbox.margin").split(" ");
            if (split.length == 1) {
                HBox.setMargin(node, new Insets(Double.parseDouble(split[0])));
            } else if (split.length == 2) {
                HBox.setMargin(node, new Insets(Double.parseDouble(split[0]), Double.parseDouble(split[1]),
                        Double.parseDouble(split[0]), Double.parseDouble(split[1])));
            } else if (split.length == 4) {
                HBox.setMargin(node, new Insets(Double.parseDouble(split[0]), Double.parseDouble(split[1]),
                        Double.parseDouble(split[2]), Double.parseDouble(split[3])));
            }
        }
    }
}
