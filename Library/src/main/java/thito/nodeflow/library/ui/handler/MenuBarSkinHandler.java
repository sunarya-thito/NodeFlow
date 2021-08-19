package thito.nodeflow.library.ui.handler;

import javafx.beans.property.*;
import javafx.scene.Node;
import javafx.scene.control.*;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;
import thito.nodeflow.library.language.*;
import thito.nodeflow.library.ui.*;

import javax.lang.model.util.*;

public class MenuBarSkinHandler implements SkinHandler<MenuBar> {
    @Override
    public void parse(SkinParser parser, MenuBar node, Element element) {
        for (Element child : element.children()) {
            MenuItem item = createMenuItem(parser, child);
            if (item instanceof Menu) {
                node.getMenus().add((Menu) item);
                Node styleableNode = item.getStyleableNode();
                if (styleableNode != null) {
                    parser.handleNode(styleableNode, child);
                }
                parser.handleMenu(item, child);
            } else throw new IllegalArgumentException("root must be a menu");
        }
    }

    public static MenuItem createMenuItem(SkinParser parser, Element element) {
        String tag = element.tagName();
        if (tag.equals("radio")) {
            RadioMenuItem item = new RadioMenuItem();
            applyAttributes(item, parser, element);
            return item;
        }
        if (tag.equals("separator")) {
            SeparatorMenuItem item = new SeparatorMenuItem();
            applyAttributes(item, parser, element);
            return item;
        }
        if (tag.equals("checkbox")) {
            CheckMenuItem item = new CheckMenuItem();
            applyAttributes(item, parser, element);
            return item;
        }
        if (tag.equals("custom")) {
            CustomMenuItem item = new CustomMenuItem();
            Element child = element.selectFirst("> *");
            if (child != null) {
                item.setContent(parser.createNode(child));
            }
            applyAttributes(item, parser, element);
            return item;
        }
        if (tag.equals("item")) {
            MenuItem item = new MenuItem();
            applyAttributes(item, parser, element);
            return item;
        }
        if (tag.equals("menu")) {
            Menu menu = new Menu();
            applyAttributes(menu, parser, element);
            for (Element child : element.select("> items > *")) {
                MenuItem item = createMenuItem(parser, child);
                menu.getItems().add(item);
                Node styleableNode = item.getStyleableNode();
                if (styleableNode != null) {
                    parser.handleNode(styleableNode, child);
                }
                parser.handleMenu(item, child);
            }
            return menu;
        }
        throw new IllegalArgumentException("invalid component "+tag);
    }

    private static void applyAttributes(MenuItem item, SkinParser parser, Element element) {
        if (element.hasAttr("class")) {
            item.getStyleClass().addAll(element.attr("class").split("\\s+"));
        }
        if (element.hasAttr("id")) {
            item.setId(element.attr("id"));
        }
        Element textNode = element.selectFirst("> text");
        if (textNode != null) {
            String text = textNode.ownText();
            if (text != null && !text.isEmpty()) {
                item.textProperty().bind(Language.getLanguage().replace(new SimpleStringProperty(text)));
            }
        }
        Element graphicNode = element.selectFirst("> graphic");
        if (graphicNode != null) {
            item.setGraphic(parser.createNode(graphicNode));
        }
    }
}
