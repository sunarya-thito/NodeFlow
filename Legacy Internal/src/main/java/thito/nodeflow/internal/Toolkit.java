package thito.nodeflow.internal;

import com.sandec.mdfx.*;
import com.sun.glass.ui.*;
import com.sun.javafx.tk.*;
import com.sun.jna.*;
import javafx.animation.*;
import javafx.beans.Observable;
import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.collections.*;
import javafx.css.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.stage.Screen;
import javafx.stage.*;
import javafx.util.*;
import org.yaml.snakeyaml.*;
import thito.nodeflow.api.*;
import thito.nodeflow.api.action.*;
import thito.nodeflow.api.config.*;
import thito.nodeflow.api.ui.Window;
import thito.nodeflow.api.ui.*;
import thito.nodeflow.api.ui.menu.MenuItem;
import thito.nodeflow.api.ui.menu.*;
import thito.nodeflow.api.ui.menu.type.*;
import thito.nodeflow.internal.config.*;
import thito.nodeflow.internal.ui.*;
import thito.nodeflow.internal.ui.menu.*;
import thito.nodeflow.internal.ui.menu.type.*;

import java.io.*;
import java.lang.reflect.*;
import java.math.*;
import java.nio.file.Path;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.function.Function;
import java.util.function.*;
import java.util.logging.*;

public class Toolkit implements thito.nodeflow.api.Toolkit {

    public static final long DATE_2020 = new Date(2020 - 1900, 0, 1).getTime();
    private static final Object NAME = new Object();
    private static Robot robot = com.sun.glass.ui.Application.GetApplication().createRobot();
    public static final Function<Paint, Background> PAINT_TO_BACKGROUND = x -> new Background(new BackgroundFill(x, null, null));
    private static Class<?> unmodifiableList, literalArrayList;
    private static Field unmodifiableList$list;
    static {
        try {
            literalArrayList = Class.forName("java.util.Arrays$ArrayList");
            unmodifiableList = Class.forName("java.util.Collections$UnmodifiableList");
            unmodifiableList$list = unmodifiableList.getDeclaredField("list");
            unmodifiableList$list.setAccessible(true);
            unfinalizeField(unmodifiableList$list);
        } catch (Throwable t) {
            throw new ReportedError(t);
        }
    }

    public static Class<?> getCaller() {
        StackTraceElement element = Thread.currentThread().getStackTrace()[3];
        try {
            return Class.forName(element.getClassName());
        } catch (Throwable t) {
            throw new ReportedError(t);
        }
    }

    private static ExecutorService sortingThread = Executors.newSingleThreadExecutor();
    public static <T> void sortInBackground(List<T> list, Comparator<T> comparator, Consumer<List<T>> then) {
        sortingThread.execute(() -> {
            try {
                Object[] array = list.toArray(new Object[0]);
                Arrays.sort(array, (Comparator) comparator);
                then.accept((List) Arrays.asList(array));
            } catch (Throwable t) {
            }
        });
    }

    public static Object defaultValue(Class<?> type) {
        if (String.class.isAssignableFrom(type)) {
            return "";
        }
        if (Character.class.isAssignableFrom(type) || char.class.equals(type)) {
            return ' ';
        }
        if (Boolean.class.isAssignableFrom(type) || boolean.class.equals(type)) {
            return false;
        }
        if (Number.class.isAssignableFrom(type) || type.isPrimitive()) {
            return 0;
        }
        return null;
    }
    /**
     * The number of bytes in a kilobyte.
     */
    public static final long ONE_KB = 1024;

    /**
     * The number of bytes in a kilobyte.
     *
     * @since 2.4
     */
    public static final BigInteger ONE_KB_BI = BigInteger.valueOf(ONE_KB);

    /**
     * The number of bytes in a megabyte.
     */
    public static final long ONE_MB = ONE_KB * ONE_KB;

    /**
     * The number of bytes in a megabyte.
     *
     * @since 2.4
     */
    public static final BigInteger ONE_MB_BI = ONE_KB_BI.multiply(ONE_KB_BI);

    /**
     * The number of bytes in a gigabyte.
     */
    public static final long ONE_GB = ONE_KB * ONE_MB;

    /**
     * The number of bytes in a gigabyte.
     *
     * @since 2.4
     */
    public static final BigInteger ONE_GB_BI = ONE_KB_BI.multiply(ONE_MB_BI);

    /**
     * The number of bytes in a terabyte.
     */
    public static final long ONE_TB = ONE_KB * ONE_GB;

    /**
     * The number of bytes in a terabyte.
     *
     * @since 2.4
     */
    public static final BigInteger ONE_TB_BI = ONE_KB_BI.multiply(ONE_GB_BI);

    /**
     * The number of bytes in a petabyte.
     */
    public static final long ONE_PB = ONE_KB * ONE_TB;

    /**
     * The number of bytes in a petabyte.
     *
     * @since 2.4
     */
    public static final BigInteger ONE_PB_BI = ONE_KB_BI.multiply(ONE_TB_BI);

    /**
     * The number of bytes in an exabyte.
     */
    public static final long ONE_EB = ONE_KB * ONE_PB;

    /**
     * The number of bytes in an exabyte.
     *
     * @since 2.4
     */
    public static final BigInteger ONE_EB_BI = ONE_KB_BI.multiply(ONE_PB_BI);

    /**
     * The number of bytes in a zettabyte.
     */
    public static final BigInteger ONE_ZB = BigInteger.valueOf(ONE_KB).multiply(BigInteger.valueOf(ONE_EB));

    /**
     * The number of bytes in a yottabyte.
     */
    public static final BigInteger ONE_YB = ONE_KB_BI.multiply(ONE_ZB);

    public static String byteCountToDisplaySize(long size) {
        return byteCountToDisplaySize(new BigInteger(Long.toString(size)));
    }

    public static String byteCountToDisplaySize(final BigInteger size) {
        final String displaySize;

        if (size.divide(ONE_EB_BI).compareTo(BigInteger.ZERO) > 0) {
            displaySize = size.divide(ONE_EB_BI) + " EB";
        } else if (size.divide(ONE_PB_BI).compareTo(BigInteger.ZERO) > 0) {
            displaySize = size.divide(ONE_PB_BI) + " PB";
        } else if (size.divide(ONE_TB_BI).compareTo(BigInteger.ZERO) > 0) {
            displaySize = size.divide(ONE_TB_BI) + " TB";
        } else if (size.divide(ONE_GB_BI).compareTo(BigInteger.ZERO) > 0) {
            displaySize = size.divide(ONE_GB_BI) + " GB";
        } else if (size.divide(ONE_MB_BI).compareTo(BigInteger.ZERO) > 0) {
            displaySize = size.divide(ONE_MB_BI) + " MB";
        } else if (size.divide(ONE_KB_BI).compareTo(BigInteger.ZERO) > 0) {
            displaySize = size.divide(ONE_KB_BI) + " KB";
        } else {
            displaySize = size + " bytes";
        }
        return displaySize;
    }
    public static <T extends Styleable> T style(T node, String... styleClass) {
        node.getStyleClass().addAll(styleClass);
        return node;
    }

    public static <T> List<T> makeItModifiable(List<T> unmodifiableList) {
        try {
            List<T> list = (List<T>) unmodifiableList$list.get(unmodifiableList);
            if (literalArrayList.isInstance(unmodifiableList)) {
                ArrayList<T> dynamicList = new ArrayList<>();
                dynamicList.addAll(list);
                unmodifiableList$list.set(unmodifiableList, dynamicList);
                return dynamicList;
            }
            if (Toolkit.unmodifiableList.isInstance(list)) {
                return makeItModifiable(list);
            }
            return list;
        } catch (Throwable t) {
            throw new ReportedError(t);
        }
    }

    public static StringConverter STRING_CONVERTER = new StringConverter() {
        Map<String, Object> cached = new HashMap<>();

        @Override
        public String toString(Object object) {
            String key;
            if (object instanceof Describable) {
                key = ((Describable) object).getDescribedName();
            } else {
                key = String.valueOf(object);
            }
            cached.put(key, object);
            return key;
        }

        @Override
        public Object fromString(String string) {
            return cached.get(string);
        }
    };

    public static <T> T infoClass(T target) {
        if (target != null) {
            info(target.getClass());
        }
        return target;
    }

    public static void centerOnScreen(Stage stage) {
        ObservableList<Screen> screen = Screen.getScreensForRectangle(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight());
        if (screen != null && screen.size() > 0) {
            float CENTER_ON_SCREEN_X_FRACTION = 1.0f / 2;
            float CENTER_ON_SCREEN_Y_FRACTION = 1.0f / 2;
            Screen main = screen.get(0);
            Rectangle2D bounds = main.getVisualBounds();
            double centerX = bounds.getMinX() + (bounds.getWidth() - stage.getWidth())
                    * CENTER_ON_SCREEN_X_FRACTION;
            double centerY = bounds.getMinY() + (bounds.getHeight() - stage.getHeight())
                    * CENTER_ON_SCREEN_Y_FRACTION;
            stage.setX(centerX);
            stage.setY(centerY);
        }
    }

    public static Point2D getCenterOfScreen(Stage stage) {
        ObservableList<Screen> screen = Screen.getScreensForRectangle(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight());
        Screen main;
        if (screen != null && screen.size() > 0) {
            main = screen.get(0);
        } else {
            main = Screen.getPrimary();
        }
        float CENTER_ON_SCREEN_X_FRACTION = 1.0f / 2;
        float CENTER_ON_SCREEN_Y_FRACTION = 1.0f / 2;
        Rectangle2D bounds = main.getVisualBounds();
        double centerX = bounds.getMinX() + (bounds.getWidth() - stage.getWidth())
                * CENTER_ON_SCREEN_X_FRACTION;
        double centerY = bounds.getMinY() + (bounds.getHeight() - stage.getHeight())
                * CENTER_ON_SCREEN_Y_FRACTION;
        return new Point2D(centerX, centerY);
    }

    public static Window getWindow(Node node) {
        Scene scene = node.getScene();
        if (scene != null) {
            javafx.stage.Window window = scene.getWindow();
            if (window != null) {
                Object windowInstance = window.getProperties().get(Window.class);
                if (windowInstance instanceof Window) {
                    return (Window) windowInstance;
                }
            }
        }
        return null;
    }

    public static <T extends Node> void debugNode(ObservableValue<T> property) {
        NodeFlow.getMainLogger().log(Level.INFO, debugHierarchy(new StringBuilder(), property.getValue()).toString());
        property.addListener((obs) -> {
            NodeFlow.getMainLogger().log(Level.INFO, debugHierarchy(new StringBuilder(), property.getValue()).toString());
        });
    }

    public static <T> T info(T value) {
        NodeFlow.getMainLogger().log(Level.INFO, String.valueOf(value));
        return value;
    }

    public static <T> T warn(T value) {
        NodeFlow.getMainLogger().log(Level.WARNING, String.valueOf(value));
        return value;
    }

    public static void error(Object message, Throwable error) {
        NodeFlow.getMainLogger().log(Level.SEVERE, String.valueOf(message), error);
        error.printStackTrace();
    }
    public static void unfinalizeField(Field field) {
        try {
            Field modifiers = Field.class.getDeclaredField("modifiers");
            modifiers.setAccessible(true);
            modifiers.set(field, field.getModifiers() & ~Modifier.FINAL);
        } catch (Throwable t) {
            throw new ReportedError(t);
        }
    }
    public static com.sun.glass.ui.Window getWindow(Stage stage) {
        try {
            TKStage tkStage = stage.impl_getPeer();
            Method getPlatformWindow = tkStage.getClass().getDeclaredMethod("getPlatformWindow" );
            getPlatformWindow.setAccessible(true);
            Object platformWindow = getPlatformWindow.invoke(tkStage);
            return (com.sun.glass.ui.Window) platformWindow;
        } catch (Throwable e) {
            System.err.println("Error getting Window Pointer");
            return null;
        }
    }
    public static Pointer getWindowPointer(javafx.stage.Window stage) {
        try {
            TKStage tkStage = stage.impl_getPeer();
            Method getPlatformWindow = tkStage.getClass().getDeclaredMethod("getPlatformWindow" );
            getPlatformWindow.setAccessible(true);
            Object platformWindow = getPlatformWindow.invoke(tkStage);
            Method getNativeHandle = platformWindow.getClass().getMethod( "getNativeHandle" );
            getNativeHandle.setAccessible(true);
            Object nativeHandle = getNativeHandle.invoke(platformWindow);
            return new Pointer((Long) nativeHandle);
        } catch (Throwable e) {
            System.err.println("Error getting Window Pointer");
            return null;
        }
    }

    private static StringBuilder debugHierarchy(StringBuilder builder, Node node) {
        if (node == null) {
            builder.append("null");
            return builder;
        }
        builder.append(node.toString());
        Parent parent = node.getParent();
        if (parent != null) {
            builder.append(" > ");
            debugHierarchy(builder, parent);
        }
        return builder;
    }

    public interface AutoCompleteComparator<T> {
        boolean matches(String typedText, T objectToCompare);
    }

    public static<T> void autoCompleteComboBoxPlus(ComboBox<T> comboBox, AutoCompleteComparator<T> comparatorMethod) {
        ObservableList<T> data = comboBox.getItems();

        comboBox.setEditable(true);
        comboBox.getEditor().focusedProperty().addListener(observable -> {
            if (comboBox.getSelectionModel().getSelectedIndex() < 0) {
                comboBox.getEditor().setText(null);
            }
        });
        comboBox.addEventHandler(KeyEvent.KEY_PRESSED, t -> comboBox.hide());
        comboBox.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {

            private boolean moveCaretToPos = false;
            private int caretPos;

            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.UP) {
                    caretPos = -1;
                    if (comboBox.getEditor().getText() != null) {
                        moveCaret(comboBox.getEditor().getText().length());
                    }
                    return;
                } else if (event.getCode() == KeyCode.DOWN) {
                    if (!comboBox.isShowing()) {
                        comboBox.show();
                    }
                    caretPos = -1;
                    if (comboBox.getEditor().getText() != null) {
                        moveCaret(comboBox.getEditor().getText().length());
                    }
                    return;
                } else if (event.getCode() == KeyCode.BACK_SPACE) {
                    if (comboBox.getEditor().getText() != null) {
                        moveCaretToPos = true;
                        caretPos = comboBox.getEditor().getCaretPosition();
                    }
                } else if (event.getCode() == KeyCode.DELETE) {
                    if (comboBox.getEditor().getText() != null) {
                        moveCaretToPos = true;
                        caretPos = comboBox.getEditor().getCaretPosition();
                    }
                } else if (event.getCode() == KeyCode.ENTER) {
                    return;
                }

                if (event.getCode() == KeyCode.RIGHT || event.getCode() == KeyCode.LEFT || event.getCode().equals(KeyCode.SHIFT) || event.getCode().equals(KeyCode.CONTROL)
                        || event.isControlDown() || event.getCode() == KeyCode.HOME
                        || event.getCode() == KeyCode.END || event.getCode() == KeyCode.TAB) {
                    return;
                }

                ObservableList<T> list = FXCollections.observableArrayList();
                for (T aData : data) {
                    if (aData != null && comboBox.getEditor().getText() != null && comparatorMethod.matches(comboBox.getEditor().getText(), aData)) {
                        list.add(aData);
                    }
                }
                String t = "";
                if (comboBox.getEditor().getText() != null) {
                    t = comboBox.getEditor().getText();
                }

                comboBox.setItems(list);
                comboBox.getEditor().setText(t);
                if (!moveCaretToPos) {
                    caretPos = -1;
                }
                moveCaret(t.length());
                if (!list.isEmpty()) {
                    comboBox.show();
                }
            }

            private void moveCaret(int textLength) {
                if (caretPos == -1) {
                    comboBox.getEditor().positionCaret(textLength);
                } else {
                    comboBox.getEditor().positionCaret(caretPos);
                }
                moveCaretToPos = false;
            }
        });
    }

    public static<T> T getComboBoxValue(ComboBox<T> comboBox){
        if (comboBox.getSelectionModel().getSelectedIndex() < 0) {
            return null;
        } else {
            return comboBox.getItems().get(comboBox.getSelectionModel().getSelectedIndex());
        }
    }

    public static void name(Node node, String name) {
        node.getProperties().put(NAME, name);
    }

    public static String name(Node node) {
        return (String) node.getProperties().getOrDefault(NAME, node.getId());
    }

    public static void install(Node node, Tooltip tooltip) {
        AtomicInteger time = new AtomicInteger();
        Timeline[] atomic = new Timeline[1];
        atomic[0] = new Timeline(new KeyFrame(Duration.millis(16), action -> {
            double mouseX = getMouseX();
            double mouseY = getMouseY();
            int tick = time.getAndIncrement();
            if (tick > 20) {
                if (tooltip.isShowing()) {
                    tooltip.setX(mouseX + 7);
                    tooltip.setY(mouseY + 7);
                } else {
                    tooltip.show(node, mouseX + 7, mouseY + 7);
                }
            }
            if (tick > 200) {
                tooltip.hide();
                atomic[0].stop();
            }
        }));
        atomic[0].setCycleCount(Animation.INDEFINITE);
        node.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
            time.set(0);
            atomic[0].play();
        });
        node.addEventHandler(MouseEvent.MOUSE_EXITED, event -> {
            tooltip.hide();
            atomic[0].stop();
        });
    }

    public static void install(Node node, SimpleContextMenu tooltip, int delay) {
        AtomicInteger time = new AtomicInteger();
        Timeline[] atomic = new Timeline[1];
        atomic[0] = new Timeline(new KeyFrame(Duration.millis(16), action -> {
            double mouseX = getMouseX();
            double mouseY = getMouseY();
            int tick = time.getAndIncrement();
            if (tick > delay) {
                if (tooltip.isShowing()) {
                    tooltip.setX(mouseX + 7);
                    tooltip.setY(mouseY + 7);
                } else {
                    tooltip.show(mouseX + 7, mouseY + 7);
                }
            }
            if (tick > 200) {
                tooltip.hide();
                atomic[0].stop();
            }
        }));
        atomic[0].setCycleCount(Animation.INDEFINITE);
        node.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
            time.set(0);
            atomic[0].play();
        });
        node.addEventHandler(MouseEvent.MOUSE_EXITED, event -> {
            tooltip.hide();
            atomic[0].stop();
        });
    }

    public static void install(Node node, Supplier<SimpleContextMenu> tooltip, boolean hover) {
        AtomicInteger time = new AtomicInteger();
        Timeline[] atomic = new Timeline[1];
        AtomicReference<SimpleContextMenu> reference = new AtomicReference<>();
        AtomicBoolean pause = new AtomicBoolean();
        atomic[0] = new Timeline(new KeyFrame(Duration.millis(16), action -> {
            double mouseX = getMouseX();
            double mouseY = getMouseY();
            double offsetX = 0;
            double offsetY = 0;
            int tick = time.getAndIncrement();
            if (tick > 50) {
                if (reference.get() != null) {
                    reference.get().setX(mouseX + 12 - offsetX);
                    reference.get().setY(mouseY + 12 - offsetY);
                    if (!reference.get().getStage().isShowing()) {
                        reference.get().hide();
                        atomic[0].stop();
                        reference.set(null);
                        return;
                    }
                } else if (!pause.get()) {
                    reference.set(tooltip.get());
                    reference.get().show(mouseX + 12 - offsetX, mouseY + 12 - offsetY);
                }
            }
            if (tick > 10000) {
                if (reference.get() != null) {
                    reference.get().hide();
                    reference.set(null);
                }
                atomic[0].stop();
            }
        }));
        atomic[0].setCycleCount(Animation.INDEFINITE);
        node.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            pause.set(true);
        });
        node.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            pause.set(false);
        });
        node.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
            time.set(0);
            atomic[0].play();
        });
        node.addEventHandler(MouseEvent.MOUSE_EXITED, event -> {
            if (reference.get() != null) {
                reference.get().hide();
                reference.set(null);
            }
            atomic[0].stop();
        });
    }

    public static Color colorToColor(thito.nodeflow.api.ui.Color color) {
        if (color instanceof ColorImpl) {
            return ((ColorImpl) color).colorProperty().get();
        }
        return Color.rgb(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()/255d);
    }

    public static TextAlignment posToTextAlignment(thito.nodeflow.api.ui.Pos pos) {
        if (pos == thito.nodeflow.api.ui.Pos.JUSTIFY) {
            return TextAlignment.JUSTIFY;
        }
        switch (pos.getHorizontal()) {
            case RIGHT: return TextAlignment.RIGHT;
            case LEFT: return TextAlignment.LEFT;
            case CENTER: return TextAlignment.CENTER;
        }
        return null; // should be impossible at this point
    }

    public static SimpleStringProperty getText(MDFXNode node) {
        // MDFX library is a poor library that this node doesn't even have text property
        try {
            Field field = MDFXNode.class.getDeclaredField("mdStringProperty");
            field.setAccessible(true);
            return (SimpleStringProperty) field.get(node);
        } catch (Throwable t) {
            throw new ReportedError(t);
        }
    }

    /**
     * Attempts to calculate the size of a file or directory.
     *
     * <p>
     * Since the operation is non-atomic, the returned value may be inaccurate.
     * However, this method is quick and does its best.
     */
    public static long size(Path path) {

        final AtomicLong size = new AtomicLong(0);

        try {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    size.addAndGet(attrs.size());
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) {
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            throw new AssertionError("walkFileTree will not throw IOException if the FileVisitor does not");
        }

        return size.get();
    }

    public static javafx.geometry.Pos posToPos(thito.nodeflow.api.ui.Pos pos) {
        switch (pos) {
            case TOP_LEFT: return javafx.geometry.Pos.TOP_LEFT;
            case TOP: return javafx.geometry.Pos.TOP_CENTER;
            case TOP_RIGHT: return javafx.geometry.Pos.TOP_RIGHT;
            case LEFT: return javafx.geometry.Pos.CENTER_LEFT;
            case CENTER: return javafx.geometry.Pos.CENTER;
            case RIGHT: return javafx.geometry.Pos.CENTER_RIGHT;
            case BOTTOM_LEFT: return javafx.geometry.Pos.BOTTOM_LEFT;
            case BOTTOM: return javafx.geometry.Pos.BOTTOM_CENTER;
            case BOTTOM_RIGHT: return javafx.geometry.Pos.BOTTOM_RIGHT;
        }
        return null;
    }

    public static <T extends Region> T clip(T region) {
        Rectangle rectangle = new Rectangle();
        rectangle.widthProperty().bind(region.widthProperty());
        rectangle.heightProperty().bind(region.heightProperty());
        region.clipProperty().set(rectangle);
        return region;
    }

    public static <F, T> ObjectBinding<T> convertBindings(Property<F> from, Function<F, T> map) {
        return new ObjectBinding<T>() {

            @Override
            protected T computeValue() {
                return map.apply(from.getValue());
            }

        };
    }

    public static void debug(Observable value) {
        NodeFlow.getApplication().getLogger().log(Level.INFO, String.valueOf(value));
        value.addListener(obs -> {
            NodeFlow.getApplication().getLogger().log(Level.INFO, String.valueOf(value));
        });
    }

    public static <X extends Styleable> CssMetaData<X, Paint> paintCssMetaData(String name, Function<X, StyleableProperty<Paint>> getter) {
        return paintCssMetaData(name, Color.TRANSPARENT, getter);
    }

    public static <X extends Styleable> CssMetaData<X, Paint> paintCssMetaData(String name, Paint defaultValue, Function<X, StyleableProperty<Paint>> getter) {
        return cssMetaData(name, StyleConverter.getPaintConverter(), defaultValue, getter);
    }

    public static <X extends Styleable, T, K> CssMetaData<X, T> cssMetaData(String name, StyleConverter converter, T defaultValue, Function<X, StyleableProperty<T>> getter) {
        return new CssMetaData<X, T>(name, converter, defaultValue) {
            @Override
            public boolean isSettable(X styleable) {
                StyleableProperty<T> property = getStyleableProperty(styleable);
                if (property instanceof Property && !((Property<?>) property).isBound()) {
                    return true;
                }
                return property.getValue() == null;
            }

            @Override
            public StyleableProperty<T> getStyleableProperty(X styleable) {
                return getter.apply(styleable);
            }
        };
    }

    public static double getMouseX() {
        return robot.getMouseX();
    }

    public static double getMouseY() {
        return robot.getMouseY();
    }

    public static int calculateSearchScore(String propComparison, String filter) {
//        return calculateSearchScore(propComparison, filter, false) * 5 + calculateSearchScore(propComparison, filter, true);
        if (filter == null || propComparison == null) return 0;
//        return computeLevenshteinDistance(propComparison.toLowerCase(), filter.toLowerCase());
        return calculateSearchScore(propComparison, filter, false);
//        return (int) (cosineTextSimilarity(propComparison.split(" "), filter.split(" ")) * 1000);
    }

    private static int minimum(int a, int b, int c)
    {
        return Math.min(Math.min(a, b), c);
    }

    public static int computeLevenshteinDistance(CharSequence str1, CharSequence str2)
    {
        int[][] distance = new int[str1.length() + 1][str2.length() + 1];

        for (int i = 0; i <= str1.length(); i++)
            distance[i][0] = i;
        for (int j = 1; j <= str2.length(); j++)
            distance[0][j] = j;

        for (int i = 1; i <= str1.length(); i++)
            for (int j = 1; j <= str2.length(); j++)
                distance[i][j] = minimum(distance[i - 1][j] + 1,
                        distance[i][j - 1] + 1,
                        distance[i - 1][j - 1] + ((str1.charAt(i - 1) == str2.charAt(j - 1)) ? 0 : 1));

        int minScore = distance[str1.length()][str2.length()];
        int maxScore = Math.max(str1.length(), str2.length());
        return maxScore - minScore;
    }

    public static double cosineTextSimilarity(String[] left, String[] right) {
        Map<String, Integer> leftWordCountMap = new HashMap<>();
        Map<String, Integer> rightWordCountMap = new HashMap<>();
        Set<String> uniqueSet = new HashSet<>();
        Integer temp;
        for (String leftWord : left) {
            temp = leftWordCountMap.get(leftWord);
            if (temp == null) {
                leftWordCountMap.put(leftWord, 1);
                uniqueSet.add(leftWord);
            } else {
                leftWordCountMap.put(leftWord, temp + 1);
            }
        }
        for (String rightWord : right) {
            temp = rightWordCountMap.get(rightWord);
            if (temp == null) {
                rightWordCountMap.put(rightWord, 1);
                uniqueSet.add(rightWord);
            } else {
                rightWordCountMap.put(rightWord, temp + 1);
            }
        }
        int[] leftVector = new int[uniqueSet.size()];
        int[] rightVector = new int[uniqueSet.size()];
        int index = 0;
        Integer tempCount;
        for (String uniqueWord : uniqueSet) {
            tempCount = leftWordCountMap.get(uniqueWord);
            leftVector[index] = tempCount == null ? 0 : tempCount;
            tempCount = rightWordCountMap.get(uniqueWord);
            rightVector[index] = tempCount == null ? 0 : tempCount;
            index++;
        }
        return cosineVectorSimilarity(leftVector, rightVector);
    }

    /**
     * The resulting similarity ranges from −1 meaning exactly opposite, to 1
     * meaning exactly the same, with 0 usually indicating independence, and
     * in-between values indicating intermediate similarity or dissimilarity.
     *
     * For text matching, the attribute vectors A and B are usually the term
     * frequency vectors of the documents. The cosine similarity can be seen as
     * a method of normalizing document length during comparison.
     *
     * In the case of information retrieval, the cosine similarity of two
     * documents will range from 0 to 1, since the term frequencies (tf-idf
     * weights) cannot be negative. The angle between two term frequency vectors
     * cannot be greater than 90°.
     *
     * @param leftVector
     * @param rightVector
     * @return
     */
    private static double cosineVectorSimilarity(int[] leftVector,
                                                  int[] rightVector) {
        if (leftVector.length != rightVector.length)
            return 1;
        double dotProduct = 0;
        double leftNorm = 0;
        double rightNorm = 0;
        for (int i = 0; i < leftVector.length; i++) {
            dotProduct += leftVector[i] * rightVector[i];
            leftNorm += leftVector[i] * leftVector[i];
            rightNorm += rightVector[i] * rightVector[i];
        }

        double result = dotProduct
                / (Math.sqrt(leftNorm) * Math.sqrt(rightNorm));
        return result;
    }

    @Deprecated
    public static int calculateSearchScore(String propComparison, String filter, boolean split) {
        if (filter == null || filter.isEmpty()) return 0;
        int score = 0;
        if (propComparison.equals(filter)) {
            score += 1000;
        } else if (propComparison.contains(filter)) {
            score += 900;
        } else if (propComparison.equalsIgnoreCase(filter)) {
            score += 800;
        } else if (propComparison.toLowerCase().contains(filter.toLowerCase())) {
            score += 700;
        }
        if (filter.contains(propComparison)) {
            score += 500;
        } else if (filter.toLowerCase().contains(propComparison.toLowerCase())) {
            score += 250;
        }
        if (split) {
            score *= 10;
            for (String prop : propComparison.split(" ")) {
                score += calculateSearchScore(prop, filter, false);
            }
        }
        return score;
    }

    public interface IgnoreError {
        void run() throws Throwable;
    }

    public interface IgnoreCallableError<T> {
        T get() throws Throwable;
    }

    public static String toCSSName(String javaName) {
        StringBuilder builder = new StringBuilder(javaName.length());
        boolean wasUppercase = true;
        for (int i = 0; i < javaName.length(); i++) {
            char current = javaName.charAt(i);
            boolean uppercase = Character.isUpperCase(current);
            if (uppercase && !wasUppercase) {
                builder.append('-');
            }
            builder.append(Character.toLowerCase(current));
            wasUppercase = uppercase;
        }
        return builder.toString();
    }

    public static <T extends Node> T applyCSS(T node) {
        node.getStyleClass().add(toCSSName(node.getClass().getSimpleName()));
        return node;
    }

    public static <T> T reportErrorLater(IgnoreCallableError<T> executable) {
        try {
            return executable.get();
        } catch (Throwable t) {
            throw new ReportedError(t);
        }
    }
    public static void reportErrorLater(IgnoreError executable) {
        try {
            executable.run();
        } catch (Throwable t) {
            throw new ReportedError(t);
        }
    }

    public static void ignoreError(IgnoreError executable) {
        try {
            executable.run();
        } catch (Throwable t) {
        }
    }

    public static void printErrorLater(IgnoreError executable) {
        try {
            executable.run();
        } catch (Throwable t) {
        }
    }

    public static <T> T errorOrNull(IgnoreCallableError<T> callable) {
        try {
            return callable.get();
        } catch (Throwable t) {
        }
        return null;
    }

    public static byte[] readAll(InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buff = new byte[1024 * 8];
        int len;
        while ((len = inputStream.read(buff, 0, buff.length)) != -1) {
            outputStream.write(buff, 0, len);
        }
        return outputStream.toByteArray();
    }
    public static <T> T selectFirst(Iterable<T> iterable, Predicate<T> filter) {
        for (T t : iterable) {
            if (filter.test(t)) return t;
        }
        return null;
    }
    public static <T> T selectLast(Iterable<T> iterable, Predicate<T> filter) {
        T selected = null;
        for (T t : iterable) {
            if (filter.test(t)) selected = t;
        }
        return selected;
    }
    public static <X extends Collection<T>, T> X collect(Iterable<T> iterable, Predicate<T> filter, X storage) {
        for (T t : iterable) {
            if (filter.test(t)) storage.add(t);
        }
        return storage;
    }
    public static <T, X> Iterable<T> collectAll(Iterable<X> x, Function<X, Iterable<T>> collector) {
        return () -> new Iterator<T>() {
            private Iterator<T> current;
            private final Iterator<X> iterator = x.iterator();
            private Iterator<T> current() {
                if (current == null) {
                    if (iterator.hasNext()) {
                        current = collector.apply(iterator.next()).iterator();
                    }
                } else {
                    if (!current.hasNext()) {
                        if (iterator.hasNext()) {
                            current = collector.apply(iterator.next()).iterator();
                        } else {
                            current = null;
                        }
                    }
                }
                return current;
            }
            @Override
            public boolean hasNext() {
                Iterator<T> current = current();
                return current != null && current.hasNext();
            }

            @Override
            public void remove() {
                Iterator<T> current = current();
                if (current != null) {
                    current.next();
                }
            }

            @Override
            public T next() {
                return current().next();
            }
        };
    }

    @Override
    public <M extends Map<K, V>, K, V> MapEditor<M, K, V> newMapEditor(M map) {
        return new MapEditorImpl<>(map);
    }

    @Override
    public void saveYaml(Section section, Writer writer) {
        DumperOptions options = new DumperOptions();
        options.setPrettyFlow(true);
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Yaml yaml = new Yaml(options);
        yaml.dump(section, writer);
    }

    @Override
    public Section loadYaml(Reader reader) {
        Object loaded = new Yaml().load(reader);
        return (Section) Section.wrapValue(loaded);
    }

    @Override
    public MapSection newDefaultMapSection() {
        return new MapSectionImpl() {
            MapSection finalizeMap() {
                map = Collections.unmodifiableMap(map);
                return this;
            }
        }.finalizeMap();
    }

    @Override
    public ListSection newDefaultListSection() {
        return new ListSectionImpl() {
            ListSection finalizeList() {
                list = Collections.unmodifiableList(list);
                return this;
            }
        }.finalizeList();
    }

    @Override
    public MapSection newMapSection(Map<?, ?> map) {
        return new MapSectionImpl(map);
    }

    @Override
    public ListSection newListSection(List<?> list) {
        return new ListSectionImpl(list);
    }

    @Override
    public MapSection newMapSection() {
        return new MapSectionImpl();
    }

    @Override
    public ListSection newListSection(Object... elements) {
        return new ListSectionImpl(elements);
    }

    @Override
    public ListSection newListSection() {
        return new ListSectionImpl();
    }

    @Override
    public MenuItem createItem(Action action, MenuItemType type) {
        return new MenuItemImpl(action, type);
    }

    @Override
    public ButtonMenuItemType menuButtonType() {
        return ButtonMenuItemTypeImpl.INSTANCE;
    }

    @Override
    public CheckBoxMenuItemType menuCheckBoxType() {
        return CheckBoxMenuItemTypeImpl.INSTANCE;
    }

    @Override
    public MenuItem createSeparatorItem() {
        return new MenuSeparatorImpl();
    }

    @Override
    public RadioButtonMenuItemType menuRadioButtonType() {
        return RadioButtonMenuItemTypeImpl.INSTANCE;
    }

    @Override
    public RadioButtonGroup createGroup(String name, int maxAmount) {
        return new RadioButtonGroupImpl(name, maxAmount);
    }
}
