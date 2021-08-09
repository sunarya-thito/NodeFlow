package thito.nodeflow.library.ui.decoration;

import javafx.application.*;
import javafx.beans.Observable;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.canvas.*;
import javafx.scene.control.*;
import javafx.scene.effect.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import thito.nodeflow.api.*;
import thito.nodeflow.api.locale.*;
import thito.nodeflow.internal.Toolkit;
import thito.nodeflow.library.ui.*;

import java.util.*;

public class AboutScreen extends Pane implements Tickable {

    private static Credit[] CREDITS = {
            new Credit("Kuda Development", "NodeFlow "+ NodeFlow.getApplication().getVersion()),
            new Credit("Developed By", "Thito Yalasatria Sunarya"),
            new Credit("Powered By", "JavaFX Technology"),
            new Credit("UI/UX Design By", "Thito Yalasatria Sunarya"),
            new Credit("Using Library", "JFoenix"),
            new Credit("Using Library", "ASM"),
            new Credit("Using Library", "SnakeYAML"),
            new Credit("", "") {
                @Override
                public String getRole() {
                    return I18n.$("translator").getString();
                }

                @Override
                public String getName() {
                    return I18n.$("translators").getString();
                }
            }
    };

    private StackPane stacked = new StackPane();

    private CanvasPane canvas = new CanvasPane();
    private Random random = new Random();

    private Pane shakyLayer = new Pane();
    private Pane activeLayer = new Pane();

    private Ship ship = new Ship();

    private int creditIndex = -1;
    private long tick = 250;
    private int bulletIndex = 0;
    private boolean shooting = false;

    private Bullet[] bullets = new Bullet[200];
    private Star[] stars;

    private int totalScore = 0;
    private Label score = new Label("Score:");
    private HBox heartBox = new HBox();
    private ImageView[] hearts = new ImageView[3];

    private Label gameOver = new Label("GAME OVER");

    public AboutScreen() {
        Toolkit.clip(this);

        Toolkit.style(gameOver, "credit-text-game-over");
        gameOver.setOpacity(0);

        for (int i = 0; i < hearts.length; i++) {
            hearts[i] = new ImageView("rsrc:images/credits/heart.png");
            hearts[i].setPreserveRatio(true);
            hearts[i].fitHeightProperty().bind(heartBox.heightProperty());
        }

        heartBox.getChildren().addAll(hearts);
        heartBox.setMinHeight(13);
        heartBox.setMaxHeight(13);
        heartBox.setSpacing(3);
        heartBox.layoutXProperty().bind(shakyLayer.widthProperty().subtract(20).subtract(heartBox.widthProperty()));
        heartBox.setLayoutY(10);

        gameOver.layoutXProperty().bind(shakyLayer.widthProperty().subtract(gameOver.widthProperty()).divide(2));
        gameOver.layoutYProperty().bind(shakyLayer.heightProperty().subtract(gameOver.heightProperty()).divide(2));

        activeLayer.prefWidthProperty().bind(shakyLayer.widthProperty());
        activeLayer.prefHeightProperty().bind(shakyLayer.heightProperty());
        shakyLayer.prefWidthProperty().bind(widthProperty());
        shakyLayer.prefHeightProperty().bind(heightProperty());

        score.setLayoutX(20);
        score.setLayoutY(10);
        Toolkit.style(score, "credit-text-score");
        shakyLayer.getChildren().addAll(activeLayer, score, heartBox, gameOver);
        stacked.getChildren().addAll(canvas, shakyLayer);

        stacked.prefWidthProperty().bind(widthProperty());
        stacked.prefHeightProperty().bind(heightProperty());
        getChildren().addAll(stacked);

        canvas.widthProperty().addListener(this::recount);
        canvas.heightProperty().addListener(this::recount);
        recount(null);
        setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));

        ship.setPrefHeight(50);
        ship.setPrefWidth(50);

        activeLayer.getChildren().add(ship);

        shakyLayer.setOnMousePressed(event -> {
            shooting = true;
            if (ship.heart <= 0) {
                newGame();
            }
        });
        shakyLayer.setOnMouseReleased(event -> shooting = false);
        newGame();
        Ticker.register(this);
    }

    private void newGame() {
        setTotalScore(0);
        score.setText(String.format("Score:  %09d", totalScore));
        ship.heart = hearts.length;
        ship.setLayoutX(-100);
        List<Node> entities = activeLayer.getChildren();
        for (int i = entities.size() - 1; i >= 0; i--) {
            Node node = entities.get(i);
            if (node instanceof CreditEntity) {
                entities.remove(i);
            }
        }
        setCursor(Cursor.NONE);
    }

    protected void setTotalScore(int score) {
        totalScore = score;
    }

    protected void onHeartChange(int heart) {
    }

    private void recount(Observable observable) {
        int x = (int) (canvas.getWidth() / 100);
        int y = (int) (canvas.getHeight() / 100);
        stars = new Star[x * y + 1];
        for (int i = 0; i < stars.length; i++) {
            stars[i] = new Star();
        }
    }

    public void tick() {
        // Mouse Handler
        Point2D point = shakyLayer.screenToLocal(Toolkit.getMouseX(), Toolkit.getMouseY());
        if (point == null) return; // not visible
        ship.targetX = Math.max(0, Math.min(shakyLayer.getWidth(), point.getX())) - ship.getWidth() / 2;
        ship.targetY = Math.max(0, Math.min(shakyLayer.getHeight(), point.getY())) - ship.getHeight() / 2;

        if (ship.heart <= 0) {
            gameOver.setOpacity(1);
            setCursor(null);
            ship.setOpacity(0);
            activeLayer.setOpacity(0.5);
        } else {
            ship.setOpacity(1);
            gameOver.setOpacity(0);
            activeLayer.setOpacity(1);
        }

        // Shaky Screen
        if (ship.heart > 0) {
            activeLayer.setLayoutX(random.nextInt(6));
            activeLayer.setLayoutY(random.nextInt(3));
        }

        // Stars Graphics
        GraphicsContext graphicsContext = canvas.getCanvas().getGraphicsContext2D();
        graphicsContext.clearRect(0, 0, getWidth(), getHeight());
        graphicsContext.setFill(Color.WHITE);
        for (int i = 0; i < stars.length; i++) {
            Star star  = stars[i];
            if (!star.isInside()) {
                star.reposition();
            }
            graphicsContext.setEffect(new MotionBlur(0, star.radius * 8));
            graphicsContext.fillOval(star.x + activeLayer.getLayoutX() * star.radius * 2, star.y + activeLayer.getLayoutY() * star.radius * 2, star.radius * 8, star.radius);
            star.x -= star.speed;
        }

        // Bullets Graphics
        graphicsContext.setFill(Color.YELLOW);
        graphicsContext.setEffect(new MotionBlur(0, 5));
        for (int i = 0; i < bullets.length; i++) {
            Bullet bullet = bullets[i];
            if (bullet != null) {
                if (bullet.y > activeLayer.getWidth()) {
                    bullets[i] = null;
                } else {
                    bullet.x += 20;
                }
                graphicsContext.fillOval(bullet.x + activeLayer.getLayoutX() * 2, bullet.y + activeLayer.getLayoutY() * 2, 20, 3);
            }
        }
        graphicsContext.setEffect(null);

        tick++;

        ship.update();

        if (tick % (ship.heart > 0 ? 350 : 100) == 0) {
            Credit credit = CREDITS[creditIndex = (creditIndex + 1) % CREDITS.length];
            CreditEntity entity = credit == CREDITS[0] ? new BossEntity() : new CreditEntity();
            activeLayer.getChildren().add(entity);
            entity.initialize(credit);
        }

        // Enemy Handling
        List<Node> entities = activeLayer.getChildren();
        for (int i = entities.size() - 1; i >= 0; i--) {
            Node node = entities.get(i);
            if (node instanceof CreditEntity) {
                CreditEntity entity = (CreditEntity) node;
                Bounds bounds = entity.localToParent(entity.labels.getBoundsInParent());
                boolean remove = false;
                boolean collision = ship.heart > 0 && ship.getBoundsInParent().intersects(bounds);
                if (collision && ship.damageTicks <= 0) {
                    entity.damage(3);
                    ship.damage();
                    // reposition
//                    double centerX = entity.getLayoutX() + entity.getWidth() / 2;
//                    double centerY = entity.getLayoutY() + entity.getHeight() / 2;
//                    double shipCenterX = ship.getLayoutX() + ship.getWidth() / 2;
//                    double shipCenterY = ship.getLayoutY() + ship.getHeight() / 2;
//                    double distanceX = shipCenterX - centerX;
//                    double distanceY = shipCenterY - centerY;
//                    double pushX = distanceX - (ship.getWidth() / 2 + entity.getWidth() / 2) * (distanceX / Math.abs(distanceX));
//                    double pushY = distanceY - (ship.getHeight() / 2 + entity.getHeight() / 2) * (distanceY / Math.abs(distanceY));
//                    ship.setLayoutX(ship.getLayoutX() + pushX);
//                    ship.setLayoutY(ship.getLayoutY() + pushY);
                }
                if (entity.health <= 0) {
                    remove = true;
                    setTotalScore(totalScore + 1);
                    score.setText(String.format("Score:  %09d", totalScore));
                }
                if (!entity.isInside()) {
                    remove = true;
                } else {
                    entity.setLayoutX(entity.getLayoutX() - 2);
                    entity.updateDamageTick();
                    for (int j = 0; j < bullets.length; j++) {
                        Bullet bullet = bullets[j];
                        if (bullet != null) {
                            if (bounds.contains(bullet.x, bullet.y)) {
                                bullets[j] = null;
                                entity.damage(1);
                            }
                        }
                    }
                }
                if (remove) {
                    entities.remove(i);
                    activeLayer.getChildren().remove(entity);
                }
            }
        }

        // Shooting Handling
        if (shooting && tick % 10 == 0 && ship.heart > 0) {
            ship.shoot();
        }
    }

    private class Star {
        private double x;
        private double y;
        private double speed;
        private double radius;

        public Star() {
            reposition();
            x = random.nextDouble() * canvas.getWidth();
        }

        private boolean isInside() {
            return x >= -10;
        }

        private void reposition() {
            x = canvas.getWidth() + random.nextDouble() * canvas.getWidth();
            y = random.nextDouble() * canvas.getHeight();
            speed = random.nextDouble() * 40;
            radius = (random.nextDouble() + 2) * (speed / 30);
        }
    }

    private class Bullet {
        private double x, y;

        public Bullet() {
            x = ship.getLayoutX() + ship.getWidth();
            y = ship.getLayoutY() + ship.getHeight() / 2;
        }
    }

    private class Ship extends Pane {
        private double targetX, targetY;
        private Image image = new Image("rsrc:images/credits/ship.png");
        private ImageView imageView = new ImageView(image);
        private int heart = 3;
        private int damageTicks;
        private Ship() {
            getChildren().add(imageView);
            imageView.fitWidthProperty().bind(widthProperty());
            imageView.fitHeightProperty().bind(heightProperty());
        }

        private ColorAdjust grayscaleEffect = new ColorAdjust(0, -1, 0, 0);

        private void update() {
            double diffX = targetX - getLayoutX();
            double diffY = targetY - getLayoutY();

            double length = Math.sqrt(diffX * diffX + diffY * diffY);
            double theta = Math.atan2(-diffX, diffY);
            double angle = Math.toDegrees((theta + Math.PI) % (Math.PI * 2));

            imageView.setEffect(new MotionBlur(angle, length / 6));

            setLayoutX(getLayoutX() + diffX / 8);
            setLayoutY(getLayoutY() + diffY / 8);
            if (damageTicks > 0) {
                damageTicks--;
                if (damageTicks % 5 == 0) {
                    if (getEffect() != null) {
                        setEffect(null);
                    } else {
                        setEffect(new ColorAdjust(0, 0, 1, 0));
                    }
                }
            } else {
                setEffect(null);
            }
            for (int i = 0; i < hearts.length; i++) {
                if (i < heart) {
                    hearts[i].setEffect(null);
                } else {
                    hearts[i].setEffect(grayscaleEffect);
                }
            }
        }

        private void damage() {
            if (damageTicks > 0) return;
            damageTicks = 50;
            heart--;
            onHeartChange(heart);
        }

        private void shoot() {
            if (damageTicks > 0) return;
            bullets[bulletIndex] =  new Bullet();
            bulletIndex = (bulletIndex + 1) % bullets.length;
        }
    }

    private class BossEntity extends CreditEntity {
        public BossEntity() {
            Toolkit.style(role, "credit-text-role-boss");
            Toolkit.style(name, "credit-text-name-boss");
            health = 25;
            maxHealth = 25;
            labels.setSpacing(-25);
            setSpacing(-25);
        }
        protected void initialize(Credit credit) {
            role.setText(credit.role);
            name.setText(credit.name);
            setLayoutX(activeLayer.getWidth());
            Platform.runLater(() -> {
                setLayoutY((activeLayer.getHeight() - getHeight()) / 2);
            });
            topBottom = -topBottom;
        }
    }

    private class CreditEntity extends VBox {

        protected VBox labels;
        protected Label role, name;
        protected int health = 10;
        protected int maxHealth = 10;
        private double damageTick;
        private Pane healthBox = new Pane();
        private Pane healthBar = new Pane();
        private int roleLength, nameLength;

        public CreditEntity() {
            setPickOnBounds(false);
            role = new Label();
            name = new Label();
            Toolkit.style(role, "credit-text-role");
            Toolkit.style(name, "credit-text-name");
            labels = new VBox(role, name);
            labels.setSpacing(-10);

            healthBox.setMinHeight(3);
            healthBox.setMaxWidth(100);
            healthBox.setMinWidth(60);
            healthBox.getChildren().add(healthBar);
            healthBar.minHeightProperty().bind(healthBox.heightProperty());

            healthBar.setBackground(new Background(new BackgroundFill(Color.RED, null, null)));
            healthBox.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));

            role.setTextFill(Color.WHITE);
            name.textFillProperty().bind(role.textFillProperty());

            setSpacing(-10);
            setAlignment(Pos.CENTER);

            labels.setAlignment(Pos.CENTER);

            getChildren().addAll(labels, healthBox);
        }

        private void damage(int damage) {
            if (damageTick > 0) return;
            damageTick = 1;
            health -= damage;
            String combined = role.getText()+"@"+name.getText();
            combined = removeChar(combined, roleLength + nameLength + 1);
            String[] split = combined.split("@", 2);
            if (split.length == 2) {
                role.setText(split[0]);
                name.setText(split[1]);
            } else {
                role.setText("");
                name.setText(split[0]);
            }
        }

        private String removeChar(String string, int length) {
            int amount = (int) Math.ceil((double)length / (maxHealth));
            return string.substring(Math.min(string.length() - 1, amount));
        }

        private void updateDamageTick() {
            if (health != maxHealth) {
                healthBox.setOpacity(1);
                healthBar.setMinWidth(Math.max(0, healthBox.getWidth() * (health / (double)maxHealth)));
            } else {
                healthBox.setOpacity(0);
            }
            if (damageTick <= 0) return;
            damageTick -= 0.1;
            role.setTextFill(Color.color(1, clamp(1 - damageTick), clamp(1 - damageTick)));
        }

        private double clamp(double x) {
            return Math.min(1, Math.max(0, x));
        }

        protected void initialize(Credit credit) {
            role.setText(credit.getRole());
            name.setText(credit.getName());
            roleLength = credit.getRole().length();
            nameLength = credit.getName().length();
            setLayoutX(activeLayer.getWidth());
            Platform.runLater(() -> {
                setLayoutY((activeLayer.getHeight() - getHeight()) / 2 + random.nextDouble() * ((activeLayer.getHeight() - getHeight()) / 2) * topBottom + getHeight() / 2);
            });
            topBottom = -topBottom;
        }

        private boolean isInside() {
            return getLayoutX() + getWidth() >= 0;
        }

    }

    private int topBottom = 1;

    private static class Credit {
        private String role, name;
        private Credit(String role, String name) {
            this.role = role;
            this.name = name;
        }
        private Credit() {
        }

        public String getRole() {
            return role;
        }

        public String getName() {
            return name;
        }
    }

}
