package thito.nodeflow.engine.node.util;

import javafx.animation.*;
import javafx.scene.Node;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.util.*;
import thito.nodeflow.engine.node.*;

import java.util.*;

public class ActiveLinkHelper {
    private final Shape shape;
    private Trail[] trails;
    private ParallelTransition parallelTransition;
    private Color from = Color.WHITE, to = Color.WHITE;
    private Pane container;
    private final PortShape handler;
    public ActiveLinkHelper(Shape shape, PortShape handler) {
        this.handler = handler;
        this.shape = shape;
        init();
        shape.layoutBoundsProperty().addListener(obs -> {
            boolean wasPlaying = playing;
            stop();
            if (wasPlaying) {
                play();
            }
        });
    }

    public void setSourceColor(Color from) {
        this.from = from;
    }

    public void setTargetColor(Color to) {
        this.to = to;
    }

    private void init() {
        int amount = (int) (Math.sqrt(Math.pow(shape.getLayoutBounds().getWidth(), 2) + Math.pow(shape.getLayoutBounds().getHeight(), 2)) / 50);
        init(amount);
    }

    private boolean playing;

    private void init(int amount) {
        setVisible(false);
        amount = Math.abs(amount);
        if (amount <= 0) amount = 1;
        trails = new Trail[amount];
        for (int i = 0; i < amount; i++) {
            trails[i] = new Trail(i);
        }
        parallelTransition = new ParallelTransition(Arrays.stream(trails).map(Trail::getComposite).toArray(Animation[]::new));
    }

    public void play() {
        if (playing) return;
        playing = true;
        setVisible(true);
        parallelTransition.playFrom(Duration.millis(Math.sqrt(Math.pow(shape.getLayoutBounds().getWidth(), 2) + Math.pow(shape.getLayoutBounds().getHeight(), 2)) * 10));
    }

    public void playImmediately() {
        if (playing) return;
        playing = true;
        setVisible(true);
        for (Trail trail : trails) trail.addImmediately();
        parallelTransition.playFrom(Duration.millis(Math.sqrt(Math.pow(shape.getLayoutBounds().getWidth(), 2) + Math.pow(shape.getLayoutBounds().getHeight(), 2)) * 10));
    }

    public void stop() {
        if (!playing) return;
        playing = false;
        setVisible(false);
        parallelTransition.stop();
        init();
    }

    public void stopImmediately() {
        if (!playing) return;
        playing = false;
        for (Trail trail : trails) trail.removeImmediately();
        parallelTransition.stop();
        init();
    }

    public void setContainer(Pane container) {
        boolean wasPlaying = playing;
        stop();
        this.container = container;
        if (wasPlaying) {
            play();
        }
    }

    private void setVisible(boolean visible) {
        if (trails == null) return;
        if (visible) {
            for (Trail trail : trails) trail.add();
        } else {
            for (Trail trail : trails) trail.remove();
        }
    }

    public class Trail {
        private final Timeline openAnimation;
        private final Timeline closeAnimation;
        private final PathTransition transition;
        private final FillTransition fill;
        private final ParallelTransition composite;
        private final Shape node;

        public Trail(int index) {
            PortShape.Handler handler = ActiveLinkHelper.this.handler.createHandler();
            handler.unbind();
            node = handler.impl_getShapePeer();
//            node = new Polygon();
//            node.getPoints().addAll(
//                    0d, -6d, // top left
//                    0d, 6d, // bottom left
//                    12d, 0d); // center right
//            node.setFill(Color.WHITE);
            node.setScaleX(0);
            node.setScaleY(0);
            transition = new PathTransition(Duration.millis(Math.sqrt(Math.pow(shape.getLayoutBounds().getWidth(), 2) + Math.pow(shape.getLayoutBounds().getHeight(), 2)) * 10), shape, node);
            transition.setInterpolator(Interpolator.LINEAR);
            transition.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
            double target = transition.getDuration().toMillis();
            fill = new FillTransition(transition.getDuration(), node, to, from);
            composite = new ParallelTransition(transition, fill);
            composite.setCycleCount(-1);
            composite.setDelay(Duration.millis(target / trails.length * index));
            openAnimation = new Timeline(new KeyFrame(Duration.millis(200), new KeyValue(node.scaleXProperty(), 1), new KeyValue(node.scaleYProperty(), 1)));
            closeAnimation = new Timeline(new KeyFrame(Duration.millis(200), new KeyValue(node.scaleXProperty(), 0), new KeyValue(node.scaleYProperty(), 0)));
            closeAnimation.setOnFinished(e -> {
                container.getChildren().remove(node);
            });
        }

        public Node getNode() {
            return node;
        }

        public ParallelTransition getComposite() {
            return composite;
        }

        public PathTransition getTransition() {
            return transition;
        }

        public void add() {
            if (container == null) return;
            container.getChildren().remove(node);
            closeAnimation.stop();
            openAnimation.stop();
            openAnimation.play();
            container.getChildren().add(node);
        }

        public void addImmediately() {
            if (container == null) return;
            container.getChildren().remove(node);
            closeAnimation.stop();
            node.setScaleX(1);
            node.setScaleY(1);
            container.getChildren().add(node);
        }

        public void removeImmediately() {
            openAnimation.stop();
            closeAnimation.stop();
            container.getChildren().remove(node);
        }

        public void remove() {
            if (container == null) return;
            openAnimation.stop();
            closeAnimation.stop();
            closeAnimation.play();
        }
    }
}