package thito.nodeflow.library.ui.handler;

import javafx.beans.property.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import org.jsoup.nodes.*;
import thito.nodeflow.library.language.*;
import thito.nodeflow.library.ui.*;

public class TabPaneSkinHandler implements SkinHandler<TabPane> {
    @Override
    public void parse(SkinParser parser, TabPane node, Element element) {
        if (element.hasAttr("side")) {
            node.setSide(Side.valueOf(element.attr("side")));
        }
        for (Element e : element.children()) {
            if (e.tagName().equals("tab")) {
                Element child = e.selectFirst("> content");
                Tab tab = new Tab();
                if (child != null) {
                    tab.setContent(parser.createNode(child));
                }
                Element text = e.selectFirst("> title");
                if (text != null) {
                    tab.textProperty().bind(Language.getLanguage().replace(new SimpleStringProperty(text.ownText())));
                }
                if (e.hasAttr("closeable")) {
                    tab.setClosable(true);
                }
                if (e.hasAttr("disabled")) {
                    tab.setDisable(true);
                }
                Element graphic = e.selectFirst("> graphic");
                if (graphic != null) {
                    tab.setGraphic(parser.createNode(graphic));
                }
                node.getTabs().add(tab);
            } else throw new IllegalArgumentException("invalid children "+e);
        }
    }
}
