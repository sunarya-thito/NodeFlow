package thito.nodeflow.internal.ui;

import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.effect.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import thito.nodeflow.internal.*;
import thito.nodeflow.library.ui.*;

import java.util.*;

public class LegacySplashUI implements Tickable {
    private List<Rect> rectangles = new ArrayList<>();
    private Pane decoration = new Pane();
    private StackPane root = new StackPane(decoration);

    public LegacySplashUI() {
        Toolkit.clip(decoration);
        decoration.setBackground(new Background(new BackgroundFill(new LinearGradient(
                0, 1, 1, 0, true, CycleMethod.REPEAT,
                new Stop(0, Color.rgb(
                        48, 48, 48
                )),
                new Stop(1, Color.rgb(
                        70, 70, 70
                ))
        ), null, null)));
        for (int i = 0; i < 2; i++) {
            addRect(true);
        }
        Ticker.register(this);
    }

    public Pane getDecoration() {
        return decoration;
    }

    public StackPane getRoot() {
        return root;
    }

    private void addRect(boolean auto) {
        decoration.getChildren().add(generate(auto));
    }

    public void tick() {
        for (int i = rectangles.size() - 1; i >= 0; i--) {
            Rect rect = rectangles.get(i);
            if (rect.getParent().getLayoutX() > root.getWidth() + 100 && rect.getParent().getLayoutY() > root.getHeight() + 100) {
                rectangles.remove(i);
                decoration.getChildren().remove(rect.getParent());
                continue;
            }
            rect.getParent().setLayoutX(rect.getParent().getLayoutX() + rect.speed * 1.5);
            rect.getParent().setLayoutY(rect.getParent().getLayoutY() + rect.speed * 1.5);
        }
        for (int i = rectangles.size(); i < 12; i++) {
            addRect(false);
        }
    }

    int clamp(int x) {
        return Math.max(0, Math.min(255, x));
    }

    private Color base = Color.rgb(
            66, 239, 245
    );

    private Group generate(boolean auto) {
        Random random = new Random();
        double height = 100 + random.nextInt(100);
        double width = height + 300 + random.nextInt(300);
        int darken = 20 - random.nextInt(70);
        Color color = Color.rgb(
                clamp((int)(base.getRed() * 255) - darken),
                clamp((int)(base.getGreen() * 255) - darken),
                clamp((int)(base.getBlue() * 255) - darken));
        Rect rect = new Rect(width, height);
        rect.speed = random.nextDouble() + 0.5;
        rect.setFill(color);
        rect.setArcWidth(Math.min(width, 40));
        rect.setArcHeight(Math.min(height, 40));
        rect.setRotate(45);
        Group group = new Group(rect);
        int decrement = 550 / 2 - random.nextInt(550);
        Bounds bounds = group.getBoundsInLocal();
        width = bounds.getWidth();
        height = bounds.getHeight();
        if (auto) {
            group.setLayoutX(width / 2 - random.nextInt((int) width));
            group.setLayoutY(height / 2 - random.nextInt((int) height));
        } else {
            group.setLayoutX(-width + decrement - 20);
            group.setLayoutY(-height - decrement - 20);
        }
        rect.setEffect(new DropShadow(25, color.darker()));
        rectangles.add(rect);
        return group;
    }

    class Rect extends Rectangle {
        private double speed;

        public Rect(double width, double height) {
            super(width, height);
        }
    }

}
