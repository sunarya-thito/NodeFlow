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

public class PathLinkStyle implements LinkStyle {
    @Override
    public Handler createHandler(NodeLink link) {
        return new Handler() {
            private final DoubleProperty sourceX = new SimpleDoubleProperty();
            private final DoubleProperty sourceY = new SimpleDoubleProperty();
            private final DoubleProperty targetX = new SimpleDoubleProperty();
            private final DoubleProperty targetY = new SimpleDoubleProperty();
            private final BooleanProperty highlight = new SimpleBooleanProperty();
            private final Path line = new Path();
            private final MoveTo startLine = new MoveTo();
            private final LineTo endStartLine = new LineTo();
            private final MoveTo verticalLine = new MoveTo();
            private final LineTo endVerticalLine = new LineTo();
            private final MoveTo endLine = new MoveTo();
            private final LineTo endEndLine = new LineTo();
            private final ActiveLinkHelper helper;
            private final ObservableSet<Object> requestHighlight = FXCollections.observableSet();

            {
                line.getElements().addAll(startLine, endStartLine, verticalLine, endVerticalLine, endLine, endEndLine);
                line.getStyleClass().addAll("NodeLink", "NodeLinkPath");
                sourceX.addListener(o -> update());
                sourceY.addListener(o -> update());
                targetX.addListener(o -> update());
                targetY.addListener(o -> update());
                line.strokeWidthProperty().addListener(o -> update());
                line.setStrokeWidth(2);
                highlight.bind(Bindings.isNotEmpty(requestHighlight));
                PseudoClass pseudoClass = PseudoClass.getPseudoClass("highlighted");
                NodeParameter any = link.getSource() == null ? link.getTarget() : link.getSource();
                helper = new ActiveLinkHelper(line, any.getHandler().getInputPort().getShape());
                helper.setContainer(link.getCanvas().getSkin().getLinkTrailLayer());
                helper.toProperty().bind(link.getSource() == null ? link.getTarget().getHandler().getInputPort().colorProperty() : link.getSource().getHandler().getOutputPort().colorProperty());
                helper.fromProperty().bind(link.getTarget() == null ? link.getSource().getHandler().getOutputPort().colorProperty() : link.getTarget().getHandler().getInputPort().colorProperty());
                highlight.addListener((obs, old, val) -> {
                    line.pseudoClassStateChanged(pseudoClass, val);
                    if (val) {
                        helper.play();
                    } else {
                        helper.stop();
                    }
                });
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
                double x1 = source.getX();
                double y1 = source.getY();
                double x2 = target.getX();
                double y2 = target.getY();
                double x = x2 - x1;
                NodeParameter any = link.getSource() == null || link.getTarget() == null ? null : link.getSource();
                double index = any == null ? 0 : Math.max(0, any.getNode().getParameters().indexOf(any)) - any.getNode().getParameters().size() / 2d;
                double scale = any == null ? 0 : index / (double) any.getNode().getParameters().size();
                startLine.setX(x1);
                startLine.setY(y1);
                double increment = scale * (x / 2);
                endStartLine.setX(x1 + x / 2 - increment);
                endStartLine.setY(y1);
                verticalLine.setX(endStartLine.getX());
                verticalLine.setY(endStartLine.getY());
                endVerticalLine.setX(x2 - x / 2 - increment);
                endVerticalLine.setY(y2);
                endLine.setX(endVerticalLine.getX());
                endLine.setY(endVerticalLine.getY());
                endEndLine.setX(x2);
                endEndLine.setY(y2);
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
