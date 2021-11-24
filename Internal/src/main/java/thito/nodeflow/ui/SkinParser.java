package thito.nodeflow.ui;

import javafx.scene.Node;
import javafx.scene.control.*;
import org.jsoup.nodes.*;
import thito.nodeflow.ui.handler.*;

import java.lang.reflect.*;
import java.util.*;

public class SkinParser {
    private static Map<String, Class<?>> defaultClassLookUp = new HashMap<>();

    private Map<String, Class<?>> classLookUp = new HashMap<>();
    private List<SkinHandler<?>> skinHandlerList = new ArrayList<>();
    private thito.nodeflow.ui.Skin skin;

    public SkinParser(Skin skin) {
        this.skin = skin;
        skinHandlerList.add(new NodeSkinHandler());
        skinHandlerList.add(new PaneSkinHandler());
        skinHandlerList.add(new BorderPaneSkinHandler());
        skinHandlerList.add(new FlowPaneSkinHandler());
        skinHandlerList.add(new LabeledSkinHandler());
        skinHandlerList.add(new SplitPaneSkinHandler());
        skinHandlerList.add(new TextSkinHandler());
        skinHandlerList.add(new TextFieldHandler());
        skinHandlerList.add(new ImageViewSkinHandler());
        skinHandlerList.add(new ScrollPaneSkinHandler());
        skinHandlerList.add(new ToggleButtonSkinHandler());
        skinHandlerList.add(new MenuBarSkinHandler());
        skinHandlerList.add(new TabPaneSkinHandler());
        skinHandlerList.add(new RegionSkinHandler());
        skinHandlerList.add(new ControlSkinHandler());
        skinHandlerList.add(new ButtonSkinHandler());
    }

    public void loadComponents(Element components) {
        if (components != null) {
            for (Element component : components.children()) {
                try {
                    Class<?> clazz = Class.forName(component.ownText());
                    if (!Node.class.isAssignableFrom(clazz)) throw new IllegalArgumentException("not a Node "+clazz.getName());
                    classLookUp.put(component.tagName(), clazz);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }
    }

    public Node createNode(Element layout) {
        Constructor<?> constructor;
        String name = layout.tagName();
        Class<?> clazz = getClass(name);
        try {
            constructor = clazz.getConstructor();
        } catch (Throwable t) {
            throw new IllegalArgumentException("class does not have default constructor "+clazz.getName());
        }
        try {
            constructor.setAccessible(true);
            Node node = (Node) constructor.newInstance();
            node.getProperties().put(AdvancedPseudoClass.class, new AdvancedPseudoClass(node));
            for (SkinHandler handler : skinHandlerList) {
                for (Type generic : handler.getClass().getGenericInterfaces()) {
                    Class<?> type = Node.class;
                    if (generic instanceof ParameterizedType) {
                        if (!SkinHandler.class.isAssignableFrom((Class<?>) ((ParameterizedType) generic).getRawType())) continue;
                        Type param = ((ParameterizedType) generic).getActualTypeArguments()[0];
                        if (param instanceof Class) {
                            type = (Class<?>) param;
                        }
                    }
                    if (type.isInstance(node)) {
                        handler.parse(this, node, layout);
                    }
                    break;
                }
            }
            return node;
        } catch (Throwable t) {
            throw new IllegalStateException("failed to create node instance", t);
        }
    }

    public void handleMenu(MenuItem item, Element element) {
        skin.handleMenu(item, element);
    }
    public void handleNode(Node node, Element element) {
        skin.handleNode(node, element);
    }

    public Class<?> getClass(String name) {
        Class<?> clazz = classLookUp.getOrDefault(name, defaultClassLookUp.get(name));
        if (clazz == null) {
            try {
                clazz = Class.forName(name, false, SkinParser.class.getClassLoader());
            } catch (Throwable t) {
            }
        }
        if (clazz == null) throw new NullPointerException("class not found "+name);
        return clazz;
    }

    static {
        String[] javafxClasses = {
                "javafx.scene.control.Accordion",
                "javafx.scene.AmbientLight",
                "javafx.scene.layout.AnchorPane",
                "javafx.scene.shape.Arc",
                "javafx.scene.chart.Axis",
                "javafx.scene.layout.BorderPane",
                "javafx.scene.shape.Box",
                "javafx.scene.control.Button",
                "javafx.scene.control.ButtonBar",
                "javafx.scene.control.ButtonBase",
                "javafx.scene.canvas.Canvas",
                "javafx.scene.chart.CategoryAxis",
                "javafx.scene.control.Cell",
                "javafx.scene.chart.Chart",
                "javafx.scene.control.CheckBox",
                "javafx.scene.control.cell.CheckBoxListCell",
                "javafx.scene.control.cell.CheckBoxTableCell",
                "javafx.scene.control.cell.CheckBoxTreeCell",
                "javafx.scene.control.cell.CheckBoxTreeTableCell",
                "javafx.scene.control.ChoiceBox",
                "javafx.scene.control.cell.ChoiceBoxListCell",
                "javafx.scene.control.cell.ChoiceBoxTableCell",
                "javafx.scene.control.cell.ChoiceBoxTreeCell",
                "javafx.scene.control.cell.ChoiceBoxTreeTableCell",
                "javafx.scene.shape.Circle",
                "javafx.scene.control.ColorPicker",
                "javafx.scene.control.ComboBox",
                "javafx.scene.control.ComboBoxBase",
                "javafx.scene.control.cell.ComboBoxListCell",
                "javafx.scene.control.cell.ComboBoxTableCell",
                "javafx.scene.control.cell.ComboBoxTreeCell",
                "javafx.scene.control.cell.ComboBoxTreeTableCell",
                "javafx.scene.shape.CubicCurve",
                "javafx.scene.shape.Cylinder",
                "javafx.scene.control.DateCell",
                "javafx.scene.control.DatePicker",
                "javafx.scene.control.DialogPane",
                "javafx.scene.shape.Ellipse",
                "javafx.scene.layout.FlowPane",
                "javafx.scene.layout.GridPane",
                "javafx.scene.Group",
                "javafx.scene.layout.HBox",
                "javafx.scene.web.HTMLEditor",
                "javafx.scene.control.Hyperlink",
                "javafx.scene.image.ImageView",
                "javafx.scene.control.IndexedCell",
                "javafx.scene.control.Label",
                "javafx.scene.control.Labeled",
                "javafx.scene.shape.Line",
                "javafx.scene.control.ListCell",
                "javafx.scene.control.ListView",
                "javafx.scene.media.MediaView",
                "javafx.scene.control.MenuBar",
                "javafx.scene.control.MenuButton",
                "javafx.scene.shape.MeshView",
                "javafx.scene.chart.NumberAxis",
                "javafx.scene.control.Pagination",
                "javafx.scene.layout.Pane",
                "javafx.scene.ParallelCamera",
                "javafx.scene.control.PasswordField",
                "javafx.scene.shape.Path",
                "javafx.scene.PerspectiveCamera",
                "javafx.scene.chart.PieChart",
                "javafx.scene.PointLight",
                "javafx.scene.shape.Polygon",
                "javafx.scene.shape.Polyline",
                "javafx.scene.control.ProgressBar",
                "javafx.scene.control.cell.ProgressBarTableCell",
                "javafx.scene.control.cell.ProgressBarTreeTableCell",
                "javafx.scene.control.ProgressIndicator",
                "javafx.scene.shape.QuadCurve",
                "javafx.scene.control.RadioButton",
                "javafx.scene.shape.Rectangle",
                "javafx.scene.layout.Region",
                "javafx.scene.control.ScrollBar",
//                "javafx.scene.control.ScrollPane",  <<-- GOT REPLACED
                "javafx.scene.control.Separator",
                "javafx.scene.shape.Shape",
                "javafx.scene.control.Slider",
                "javafx.scene.shape.Sphere",
                "javafx.scene.control.Spinner",
                "javafx.scene.control.SplitMenuButton",
                "javafx.scene.control.SplitPane",
                "javafx.scene.layout.StackPane",
                "javafx.scene.shape.SVGPath",
                "javafx.embed.swing.SwingNode",
                "javafx.scene.control.TableCell",
                "javafx.scene.control.TableRow",
                "javafx.scene.control.TableView",
                "javafx.scene.control.TabPane",
                "javafx.scene.text.Text",
                "javafx.scene.control.TextArea",
                "javafx.scene.control.TextField",
                "javafx.scene.control.cell.TextFieldListCell",
                "javafx.scene.control.cell.TextFieldTableCell",
                "javafx.scene.control.cell.TextFieldTreeCell",
                "javafx.scene.control.cell.TextFieldTreeTableCell",
                "javafx.scene.text.TextFlow",
                "javafx.scene.layout.TilePane",
                "javafx.scene.control.TitledPane",
                "javafx.scene.control.ToggleButton",
                "javafx.scene.control.ToolBar",
                "javafx.scene.control.TreeCell",
                "javafx.scene.control.TreeTableCell",
                "javafx.scene.control.TreeTableRow",
                "javafx.scene.control.TreeTableView",
                "javafx.scene.control.TreeView",
                "javafx.scene.chart.ValueAxis",
                "javafx.scene.layout.VBox",
                "javafx.scene.control.skin.VirtualFlow",
                "javafx.scene.web.WebView",
        };
        for (String c : javafxClasses) {
            try {
                Class<?> clazz = Class.forName(c, false, SkinParser.class.getClassLoader());
                defaultClassLookUp.put(clazz.getSimpleName().toLowerCase(), clazz);
            } catch (Throwable t) {
            }
        }
        // REPLACEMENT
        defaultClassLookUp.put("scrollpane", ScrollPane.class);
    }
}
