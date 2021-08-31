package thito.nodeflow.engine.node.skin;

import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.collections.*;
import javafx.css.*;
import javafx.geometry.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import thito.nodeflow.engine.node.*;

import java.util.*;

public class NodeCanvasSkin extends Skin {

    private static final CssMetaData<NodeCanvasSkin, Color> metaBackgroundColor =
            meta("-fx-bg-color", StyleConverter.getColorConverter(), Color.BLACK, NodeCanvasSkin::backgroundColorProperty);
    private static final CssMetaData<NodeCanvasSkin, Color> metaThinLineColor =
            meta("-fx-thin-line-color", StyleConverter.getColorConverter(), Color.BLACK, NodeCanvasSkin::thinLineColorProperty);
    private static final CssMetaData<NodeCanvasSkin, Color> metaThickLineColor =
            meta("-fx-thick-line-color", StyleConverter.getColorConverter(), Color.BLACK, NodeCanvasSkin::thickLineColorProperty);
    private final StyleableObjectProperty<Color> backgroundColor =
            new SimpleStyleableObjectProperty<>(metaBackgroundColor, Color.BLACK);
    private final StyleableObjectProperty<Color> thinLineColor =
            new SimpleStyleableObjectProperty<>(metaThinLineColor, Color.BLACK);
    private final StyleableObjectProperty<Color> thickLineColor =
            new SimpleStyleableObjectProperty<>(metaThickLineColor, Color.BLACK);

    private final Rectangle selectionPane = new Rectangle();
    private final Pane selectionLayer = new Pane(selectionPane);
    private final Pane groupLayer = new Pane();
    private final Pane nodeLayer = new Pane();
    private final Pane linkLayer = new Pane();
    private final Pane groupBackgroundLayer = new Pane();
    private final Pane linkingLayer = new Pane();
    private final Pane linkTrailLayer = new Pane();
    private final ObjectProperty<BoundingBox> selection = new SimpleObjectProperty<>();
    private final ObservableList<NodeLinking> linkingList = FXCollections.observableArrayList();

    private double selectX;
    private double selectY;
    private boolean multiSelect;

    private final NodeCanvas canvas;

    public NodeCanvasSkin(NodeCanvas canvas) {
        this.canvas = canvas;
        getChildren().addAll(groupBackgroundLayer, linkTrailLayer, linkLayer, nodeLayer, linkingLayer, groupLayer, selectionLayer);
        selectionLayer.setMouseTransparent(true);
        linkingLayer.setMouseTransparent(true);
        groupBackgroundLayer.setMouseTransparent(true);
        linkTrailLayer.setMouseTransparent(true);
        skin(selectionPane, "SelectionPane");
        for (int i = 0; i < getChildren().size(); i++) {
            Pane child = (Pane) getChildren().get(i);
            child.minWidthProperty().bind(widthProperty());
            child.minHeightProperty().bind(heightProperty());
            child.maxWidthProperty().bind(widthProperty());
            child.maxHeightProperty().bind(heightProperty());
            child.setPickOnBounds(false);
        }

        addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            event.consume();
            canvas.getSelectedElements().forEach(x -> x.selectedProperty().set(false));
            selectX = event.getX();
            selectY = event.getY();
            multiSelect = event.isShiftDown();
        });

        addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
            event.consume();
            selection.set(new BoundingBox(Math.min(selectX, event.getX()), Math.min(selectY, event.getY()), Math.abs(event.getX() - selectX), Math.abs(event.getY() - selectY)));
        });

        addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            event.consume();
            selection.set(null);
        });

        addEventHandler(MouseEvent.MOUSE_MOVED, event -> {
            for (NodeLink link : getCanvas().getNodeLinkedList()) {
                LinkStyle.Handler handler = link.styleHandlerProperty().get();
                if (handler != null) {
                    boolean result = handler.impl_getPeer().contains(handler.impl_getPeer().sceneToLocal(event.getSceneX(), event.getSceneY()));
                    if (result) {
                        handler.requestHighlight().add(link);
                    } else {
                        handler.requestHighlight().remove(link);
                    }
                }
            }
        });

        selectionPane.setVisible(false);
        selection.addListener((obs, old, val) -> {
            if (val != null) {
                selectionPane.setVisible(true);
                selectionPane.setLayoutX(val.getMinX());
                selectionPane.setLayoutY(val.getMinY());
                selectionPane.setWidth(val.getWidth());
                selectionPane.setHeight(val.getHeight());
                for (CanvasElement e : canvas.getElements()) {
                    if (val.contains(e.xProperty().get(), e.yProperty().get(), e.getSkin().getWidth(), e.getSkin().getHeight())) {
                        e.selectedProperty().set(true);
                    } else if (!multiSelect) {
                        e.selectedProperty().set(false);
                    }
                }
            } else {
                selectionPane.setVisible(false);
            }
        });

        backgroundColor.addListener(this::updateBackground);
        thinLineColor.addListener(this::updateBackground);
        thickLineColor.addListener(this::updateBackground);
        updateBackground(null);

        linkingList.addListener(new NodeLinkListListener());
    }

    public Pane getLinkTrailLayer() {
        return linkTrailLayer;
    }

    public Pane getLinkingLayer() {
        return linkingLayer;
    }

    public NodeCanvas getCanvas() {
        return canvas;
    }

    public void beginLinkingDrag(double sceneX, double sceneY, NodeParameter source, NodeParameter target, boolean forceCreate) {
        if (source != null) {
            Collection<? extends NodeParameter> pairs = source.getPairs(false);
            if (pairs.isEmpty() || forceCreate) {
                NodeLinking linking = new NodeLinking(getCanvas(), source, null);
                linking.tailXProperty().set(sceneX);
                linking.tailYProperty().set(sceneY);
                linkingList.add(linking);
            } else {
                for (NodeParameter paired : pairs) {
                    getCanvas().disconnect(source, paired);
                    NodeLinking linking = new NodeLinking(getCanvas(), null, paired);
                    linking.tailXProperty().set(sceneX);
                    linking.tailYProperty().set(sceneY);
                    linkingList.add(linking);
                }
            }
        } else if (target != null) {
            Collection<? extends NodeParameter> pairs = target.getPairs(true);
            if (pairs.isEmpty() || forceCreate) {
                NodeLinking linking = new NodeLinking(getCanvas(), null, target);
                linking.tailXProperty().set(sceneX);
                linking.tailYProperty().set(sceneY);
                linkingList.add(linking);
            } else {
                for (NodeParameter paired : pairs) {
                    getCanvas().disconnect(paired, target);
                    NodeLinking linking = new NodeLinking(getCanvas(), paired, null);
                    linking.tailXProperty().set(sceneX);
                    linking.tailYProperty().set(sceneY);
                    linkingList.add(linking);
                }
            }
        }
    }

    public void dragLinking(double sceneX, double sceneY) {
        for (NodeLinking linking : linkingList) {
            linking.tailXProperty().set(sceneX);
            linking.tailYProperty().set(sceneY);
        }
    }

    public void stopLinkingDrag(double sceneX, double sceneY, boolean proceed) {
        if (proceed) {
            EventNode eventNode = getCanvas().getEventNode();
            if (eventNode != null) {
                for (NodeParameter parameter : eventNode.getParameters()) {
                    NodeParameterSkin skin = parameter.getSkin();
                    if (skin.contains(skin.sceneToLocal(sceneX, sceneY))) {
                        for (NodeLinking linking : linkingList) {
                            if (linking.getSource() != null) {
                                getCanvas().connect(linking.getSource(), parameter, false);
                            } else {
                                getCanvas().connect(parameter, linking.getTarget(), false);
                            }
                        }
                        linkingList.clear();
                        return;
                    }
                }
            }
            for (Node node : getCanvas().getNodeList()) {
                for (NodeParameter parameter : node.getParameters()) {
                    NodeParameterSkin skin = parameter.getSkin();
                    if (skin.contains(skin.sceneToLocal(sceneX, sceneY))) {
                        for (NodeLinking linking : linkingList) {
                            if (linking.getSource() != null) {
                                getCanvas().connect(linking.getSource(), parameter, false);
                            } else {
                                getCanvas().connect(parameter, linking.getTarget(), false);
                            }
                        }
                        linkingList.clear();
                        return;
                    }
                }
            }
        }
        linkingList.clear();
    }

    private void updateBackground(Observable observable) {
        WritableImage img = new WritableImage(100, 100);
        PixelWriter writer = img.getPixelWriter();
        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < 100; j++) {
                if (i < 2 || j < 2) {
                    writer.setColor(i, j, thickLineColor.get());
                } else if (i % 20 == 0 || j % 20 == 0) {
                    writer.setColor(i, j, thinLineColor.get());
                } else {
                    writer.setColor(i, j, backgroundColor.get());
                }
            }
        }
        setBackground(new Background(new BackgroundImage(img, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT)));
    }

    public StyleableObjectProperty<Color> thickLineColorProperty() {
        return thickLineColor;
    }

    public StyleableObjectProperty<Color> thinLineColorProperty() {
        return thinLineColor;
    }

    public StyleableObjectProperty<Color> backgroundColorProperty() {
        return backgroundColor;
    }

    public void onNodeAdded(Node node) {
        nodeLayer.getChildren().add(node.getSkin());
        if (node instanceof EventNode) {
            node.xProperty().set(0);
            node.yProperty().set(0);
            EventNodeSkin skin = ((EventNode) node).getSkin();
            skin.prefHeightProperty().bind(heightProperty());
        }
    }

    public void onNodeRemoved(Node node) {
        nodeLayer.getChildren().remove(node.getSkin());
        if (node instanceof EventNode) {
            EventNodeSkin skin = ((EventNode) node).getSkin();
            skin.prefHeightProperty().unbind();
        }
    }

    public void onGroupAdded(NodeGroup group) {
        groupLayer.getChildren().add(group.getSkin());
        groupBackgroundLayer.getChildren().add(group.getSkin().getBackdropSkin());
    }

    public void onGroupRemoved(NodeGroup group) {
        groupLayer.getChildren().remove(group.getSkin());
        groupBackgroundLayer.getChildren().remove(group.getSkin().getBackdropSkin());
    }

    private final DynamicNodeLink nodeLinkListener = new DynamicNodeLink(this);

    public void onLinkAdded(NodeLinked link) {
        link.styleHandlerProperty().get().getActiveLink().stopImmediately();
        nodeLinkListener.changed(link.styleHandlerProperty(), null, link.styleHandlerProperty().get());
        link.styleHandlerProperty().addListener(nodeLinkListener);
    }

    public void onLinkRemoved(NodeLinked link) {
        link.styleHandlerProperty().get().getActiveLink().stopImmediately();
        link.styleHandlerProperty().removeListener(nodeLinkListener);
        nodeLinkListener.changed(link.styleHandlerProperty(), link.styleHandlerProperty().get(), null);
    }

    public class NodeLinkListListener implements ListChangeListener<NodeLink> {
        @Override
        public void onChanged(Change<? extends NodeLink> c) {
            while (c.next()) {
                if (c.wasAdded()) {
                    for (NodeLink a : c.getAddedSubList()) {
                        linkLayer.getChildren().add(a.styleHandlerProperty().get().impl_getPeer());
                    }
                }
                if (c.wasRemoved()) {
                    for (NodeLink a : c.getRemoved()) {
                        linkLayer.getChildren().remove(a.styleHandlerProperty().get().impl_getPeer());
                    }
                }
            }
        }
    }

    public static class DynamicNodeLink implements ChangeListener<LinkStyle.Handler> {

        private final NodeCanvasSkin skin;

        public DynamicNodeLink(NodeCanvasSkin skin) {
            this.skin = skin;
        }

        @Override
        public void changed(ObservableValue<? extends LinkStyle.Handler> observable, LinkStyle.Handler oldValue, LinkStyle.Handler newValue) {
            if (oldValue != null) {
                skin.linkLayer.getChildren().remove(oldValue.impl_getPeer());
            }
            if (newValue != null) {
                skin.linkLayer.getChildren().add(newValue.impl_getPeer());
            }
        }
    }

}
