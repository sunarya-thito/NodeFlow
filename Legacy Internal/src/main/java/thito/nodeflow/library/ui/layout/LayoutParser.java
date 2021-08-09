package thito.nodeflow.library.ui.layout;

import com.jfoenix.controls.*;
import javafx.scene.Node;
import javafx.scene.*;
import javafx.scene.control.*;
import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;
import thito.nodeflow.library.ui.*;
import thito.nodeflow.library.ui.docker.*;
import thito.nodeflow.library.ui.layout.handler.attribute.*;
import thito.nodeflow.library.ui.layout.handler.child.*;
import thito.nodeflow.library.ui.layout.resolver.*;
import thito.nodeflow.library.ui.layout.tag.*;

import java.nio.charset.*;
import java.util.*;

public class LayoutParser {
    private ComponentResolver standardResolver = new JavaFXComponentResolver();
    private List<ComponentResolver> resolvers = new ArrayList<>();
    private List<ComponentAttributeHandler> attributeHandlers = new ArrayList<>();
    private Map<Class<?>, ComponentChildHandler<?>> childHandlerMap = new HashMap<>();

    public LayoutParser() {
        attributeHandlers.add(new DockerAttributeHandler());
        attributeHandlers.add(new SplitPaneAttributeHandler());
        attributeHandlers.add(new BorderPaneAttributeHandler());
        attributeHandlers.add(new StandardAttributeHandler());
        attributeHandlers.add(new TextAttributeHandler());
        attributeHandlers.add(new ImageViewAttributeHandler());
        attributeHandlers.add(new TagAttributeHandler());
        attributeHandlers.add(new BoxAttributeHandler());
        attributeHandlers.add(new TabAttributeHandler());
        attributeHandlers.add(new JFXAttributeHandler());
        childHandlerMap.put(Docker.class, new DockerChildHandler());
        childHandlerMap.put(SplitPane.class, new SplitPaneChildHandler());
        childHandlerMap.put(BetterSplitPane.class, new SplitPaneChildHandler());
        childHandlerMap.put(Group.class, new GroupChildHandler());
        childHandlerMap.put(ScrollPane.class, new ScrollPaneChildHandler());
        childHandlerMap.put(ModernScrollPane.class, new ModernScrollPaneChildHandler());
        childHandlerMap.put(Tag.class, new TagChildHandler());
        childHandlerMap.put(JFXRippler.class, new RipplerChildHandler());
        childHandlerMap.put(JFXTabPane.class, new TabPaneChildHandler());
        childHandlerMap.put(Tab.class, new TabChildHandler());
        resolvers.add(new ExternalComponentResolver());
    }

    public Map<Class<?>, ComponentChildHandler<?>> getChildHandlerMap() {
        return childHandlerMap;
    }

    public List<ComponentAttributeHandler> getAttributeHandlers() {
        return attributeHandlers;
    }

    public List<ComponentResolver> getComponentResolvers() {
        return resolvers;
    }

    private ComponentFactory resolveComponentType(String typeName) {
        for (ComponentResolver resolver : resolvers) {
            ComponentFactory test = resolver.createFactory(this, typeName);
            if (test != null) {
                return test;
            }
        }
        return standardResolver.createFactory(this, typeName);
    }

    public Document parseXML(byte[] xmlBytes) {
        return Jsoup.parse(new String(xmlBytes, StandardCharsets.UTF_8));
    }

    public Document parseXML(String string) {
        return Jsoup.parse(string);
    }

    public void parseLayout(Document document, UIComponent base) throws LayoutParserException {
        Map<String, ComponentFactory> factoryMap = new HashMap<>();
        Element components = document.selectFirst("NodeFlow > components");
        Element layout = document.selectFirst("NodeFlow > layout");
        if (components != null) {
            for (Element child : components.children()) {
                String tagName = child.tagName();
                String ownText = child.ownText();
                if (tagName != null && ownText != null) {
                    ComponentFactory factory = resolveComponentType(ownText);
                    if (factory == null) {
                        throw new LayoutParserException("unknown component type: " + ownText);
                    }
                    factoryMap.put(tagName, factory);
                }
            }
        }
        if (layout != null) {
            Elements children = layout.children();
            if (!children.isEmpty()) {
                for (Element child : children) {
                    Object parsed = parseComponent(factoryMap, child, null, base, base);
                    if (parsed instanceof Node) {
                        base.addComponent((Node) parsed);
                    }
                }
                return;
            }
        }
        return;
    }

    protected Object parseComponent(
            Map<String, ComponentFactory> factoryMap,
            Element parent,
            ComponentChildHandler parentHandler,
            Object parentComponent,
            UIComponent base
    ) throws LayoutParserException {
        String tagName = parent.tagName();
        ComponentFactory componentFactory = factoryMap.get(tagName);
        if (componentFactory == null) {
            throw new LayoutParserException("unknown component: "+tagName);
        }
        Object component = componentFactory.createComponent();
        if (component == null) {
            throw new LayoutParserException("null component: "+tagName);
        }
        if (parentHandler != null) {
            parentHandler.handleChild(parentComponent, component);
        }
        ComponentAttributeHandler attributeHandler = componentFactory.getAttributeHandler();
        if (attributeHandler != null) {
            for (Attribute attribute : parent.attributes()) {
                if (component instanceof Node) {
                    ((Node) component).getProperties().put(attribute.getKey(), attribute.getValue());
                }
                attributeHandler.handle(attribute.getKey(), attribute.getValue(), component);
            }
            String value = parent.ownText();
            if (!value.isEmpty()) {
                attributeHandler.handle("value", value, component);
            }
        }
        ComponentChildHandler<?> childHandler = componentFactory.getChildrenHandler();
        if (childHandler != null) {
            for (Element child : parent.children()) {
                parseComponent(factoryMap, child, childHandler, component, base);
            }
        }
        if (component instanceof Node) {
            base.initializeComponent((Node) component);
        }
        return component;
    }
}
