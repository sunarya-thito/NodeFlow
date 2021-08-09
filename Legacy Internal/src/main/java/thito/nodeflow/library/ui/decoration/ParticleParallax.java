package thito.nodeflow.library.ui.decoration;

import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.canvas.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import thito.nodeflow.internal.*;
import thito.nodeflow.library.ui.*;

import java.util.*;

public class ParticleParallax extends Pane implements Tickable {
    private Canvas canvas = new Canvas();
    private DoubleProperty range = new SimpleDoubleProperty(100);
    private DoubleProperty pulseRange = new SimpleDoubleProperty(100);
    private DoubleProperty pushRange = new SimpleDoubleProperty(50);
    private Random random = new Random();
    private ObservableList<Dot> dots = FXCollections.observableArrayList();

    public ParticleParallax() {
        Toolkit.style(this, "particle-parallax");
        canvas.widthProperty().bind(widthProperty());
        canvas.heightProperty().bind(heightProperty());
        widthProperty().addListener(this::updateDotAmount);
        heightProperty().addListener(this::updateDotAmount);
        getChildren().add(canvas);
        Ticker.register(this);
    }

    public DoubleProperty rangeProperty() {
        return range;
    }

    public DoubleProperty pulseRangeProperty() {
        return pulseRange;
    }

    public DoubleProperty pushRangeProperty() {
        return pushRange;
    }

    private void updateDotAmount(Observable observable) {
        int x = (int) (getWidth() / (range.get() / 1));
        int y = (int) (getHeight() / (range.get() / 1));
        int amount = x * y;
        for (int i = amount; i < dots.size(); i++) {
            dots.remove(0);
        }
        for (int i = dots.size(); i < amount; i++) {
            dots.add(new Dot());
        }
    }

    private static double clamp(double x) {
        return Math.max(0d, Math.min(1d, x));
    }

    public void tick() {
        double mouseX = Toolkit.getMouseX();
        double mouseY = Toolkit.getMouseY();
        Point2D localMouse = screenToLocal(mouseX, mouseY);
        if (localMouse == null) return; // doesn't have parent
        mouseX = localMouse.getX();
        mouseY = localMouse.getY();
        GraphicsContext context = canvas.getGraphicsContext2D();
        context.clearRect(0, 0, getWidth(), getHeight());
        context.setLineWidth(1.5);
        context.setLineCap(StrokeLineCap.ROUND);
        for (int i = 0; i < dots.size(); i++) {
            Dot dot = dots.get(i);
            int totalConnection = 0;
            for (int j = 0; j < dots.size(); j++) {
                Dot other = dots.get(j);
                if (other != dot) {
                    double distanceX = dot.x - other.x;
                    double distanceY = dot.y - other.y;
                    double distance = Math.sqrt(Math.pow(other.x - dot.x, 2) + Math.pow(other.y - dot.y, 2));
                    if (distance < range.get()) {
                        double opacity = (range.get() - distance) / range.get();
                        context.setStroke(Color.color(1, 1, 1, clamp(0.5 * dot.opacity * opacity)));
                        context.strokeLine(dot.x, dot.y, other.x, other.y);
                        if (other.isInside() || dot.isInside()) {
                            totalConnection++;
                        }
                    }
                    if (distance < pushRange.get()) {
                        double speed = (pushRange.get() - distance) / range.get();
                        dot.velocityX += (distanceX * speed) / 32;
                        dot.velocityY += (distanceY * speed) / 32;
                    }
                }
            }
            if (!dot.isInside() && totalConnection < 2) {
                dot.initialize();
                dot.opacity = 0;
            }
            context.setFill(Color.color(1, 1, 1, clamp(0.7 * dot.opacity)));
            context.fillOval(
                    dot.x - dot.radius, dot.y - dot.radius,
                    dot.radius * 2, dot.radius * 2
            );
            double mouseDistanceX = dot.x - mouseX;
            double mouseDistanceY = dot.y - mouseY;
            double mouseDistance = Math.sqrt((mouseDistanceX * mouseDistanceX) + (mouseDistanceY * mouseDistanceY));
            if (mouseDistance < pulseRange.get()) {
                dot.velocityX += (mouseDistanceX * ((pulseRange.get() - mouseDistance) / pulseRange.get())) / 16;
                dot.velocityY += (mouseDistanceY * ((pulseRange.get() - mouseDistance) / pulseRange.get())) / 16;
            }
            dot.x += dot.directionX * dot.speed + dot.velocityX;
            dot.y += dot.directionY * dot.speed + dot.velocityY;
            dot.velocityX /= 16 / 2;
            dot.velocityY /= 16 / 2;
            if (totalConnection >= 3) {
                if (dot.opacity < 1) {
                    dot.opacity += 0.05;
                }
            } else {
                if (dot.opacity > 0) {
                    dot.opacity -= 0.05;
                }
            }
        }
    }

    private double randomX() {
        return random.nextDouble() * getWidth();
    }

    private double randomY() {
        return random.nextDouble() * getHeight();
    }

    private class Dot {
        private double x = randomX(), y = randomY();
        private double directionX;
        private double directionY;
        private double velocityX;
        private double velocityY;
        private double speed;
        private double radius;
        private double opacity = 0;

        public Dot() {
            initialize();
        }

        private void initialize() {
            x = randomX();
            y = randomY();
            directionX = (random.nextDouble() - 0.5) * 2;
            directionY = (random.nextDouble() - 0.5) * 2;
            speed = random.nextDouble();
            radius = 2 + random.nextDouble() * 2;
        }

        private boolean isInside() {
            return radius <= x && radius <= y &&
                    x <= widthProperty().get() + radius &&
                    y <= heightProperty().get() + radius;
        }
    }

}
