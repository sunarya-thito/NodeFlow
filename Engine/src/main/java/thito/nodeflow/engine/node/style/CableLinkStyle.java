package thito.nodeflow.engine.node.style;

import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.css.*;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import thito.nodeflow.engine.node.*;
import thito.nodeflow.engine.node.util.*;

public class CableLinkStyle implements LinkStyle {
    @Override
    public Handler createHandler(NodeLink link) {
        return new Handler() {
            private final ActiveLinkHelper helper;
            private final DoubleProperty sourceX = new SimpleDoubleProperty();
            private final DoubleProperty sourceY = new SimpleDoubleProperty();
            private final DoubleProperty targetX = new SimpleDoubleProperty();
            private final DoubleProperty targetY = new SimpleDoubleProperty();
            private final BooleanProperty highlight = new SimpleBooleanProperty();
            private final ObservableSet<Object> requestHighlight = FXCollections.observableSet();
            private final CubicCurve line = new CubicCurve();

            {
                line.getStyleClass().addAll("NodeLink", "NodeLinkBezier");
                sourceX.addListener(o -> update());
                sourceY.addListener(o -> update());
                targetX.addListener(o -> update());
                targetY.addListener(o -> update());
                highlight.bind(Bindings.isNotEmpty(requestHighlight));
                line.setFill(Color.TRANSPARENT);
                PseudoClass pseudoClass = PseudoClass.getPseudoClass("highlighted");

                NodeParameter any = link.getSource() == null ? link.getTarget() : link.getSource();
                helper = new ActiveLinkHelper(line, any.getHandler().getInputPort().getShape());
                helper.setContainer(link.getCanvas().getSkin().getLinkTrailLayer());
                helper.setTargetColor(link.getSource() == null ? link.getTarget().getHandler().getInputPort().getColor() : link.getSource().getHandler().getOutputPort().getColor());
                helper.setSourceColor(link.getTarget() == null ? link.getSource().getHandler().getOutputPort().getColor() : link.getTarget().getHandler().getInputPort().getColor());
                highlight.addListener((obs, old, val) -> {
                    line.pseudoClassStateChanged(pseudoClass, val);
                    if (val) {
                        helper.play();
                    } else {
                        helper.stop();
                    }
                });
                line.setStrokeWidth(2);
            }

            @Override
            public ActiveLinkHelper getActiveLink() {
                return helper;
            }

            @Override
            public ObservableSet<Object> requestHighlight() {
                return requestHighlight;
            }

            @Override
            public BooleanProperty highlightProperty() {
                return highlight;
            }

            @Override
            public ObjectProperty<Paint> fillProperty() {
                return line.strokeProperty();
            }

            @Override
            public void update() {
                Point2D source = line.sceneToLocal(sourceX.get(), sourceY.get());
                Point2D target = line.sceneToLocal(targetX.get(), targetY.get());
                line.startXProperty().set(source.getX());
                line.startYProperty().set(source.getY());
                line.endXProperty().set(target.getX());
                line.endYProperty().set(target.getY());
                double x = target.getX() - source.getX();
                line.setControlX1(source.getX() + x / 2d);
                line.setControlX2(target.getX() - x / 2d);
                line.setControlY1(source.getY());
                line.setControlY2(target.getY());
            }

            @Override
            public DoubleProperty sourceXProperty() {
                return sourceX;
            }

            @Override
            public DoubleProperty sourceYProperty() {
                return sourceY;
            }

            @Override
            public DoubleProperty targetXProperty() {
                return targetX;
            }

            @Override
            public DoubleProperty targetYProperty() {
                return targetY;
            }

            @Override
            public Node impl_getPeer() {
                return line;
            }
        };
    }
}
