package thito.nodeflow.ui;

import it.unimi.dsi.fastutil.ints.*;
import javafx.beans.property.*;
import javafx.css.*;
import javafx.scene.image.*;
import javafx.scene.paint.*;

import java.util.function.*;

public class UIHelper {
    public static <T extends Styleable, K> CssMetaData<T, K> meta(String name, StyleConverter<?, K> converter, K defaultValue, Function<T, StyleableProperty<K>> function) {
        return new CssMetaData<>(name, converter, defaultValue) {
            @Override
            public boolean isSettable(T t) {
                StyleableProperty<K> property = getStyleableProperty(t);
                return property == null || (property instanceof Property && !((Property<?>) property).isBound());
            }

            @Override
            public StyleableProperty<K> getStyleableProperty(T t) {
                return function.apply(t);
            }
        };
    }

    public static Color getDominantColor(Image image, int strength) {
        Int2IntMap map = new Int2IntArrayMap();
        PixelReader reader = image.getPixelReader();
        double width = image.getWidth();
        double height = image.getHeight();
        double length = width * height;
        for (int i = 0; i < length; i++) {
            int x = (int) (i / width);
            int y = (int) (i % height);
            Color color = reader.getColor(x, y);
            if (color.getOpacity() < 1) continue;
            int red = (int) color.getRed();
            int green = (int) color.getGreen();
            int blue = (int) color.getBlue();
            red &= strength;
            green &= strength;
            blue &= strength;
            int rgb = new java.awt.Color(red, green, blue).getRGB();
            map.put(rgb, map.getOrDefault(rgb, 0) + 1);
        }
        int key = 0;
        int high = 0;
        for (Int2IntMap.Entry o : map.int2IntEntrySet()) {
            if (high < o.getIntValue()) {
                key = o.getIntKey();
                high = o.getIntValue();
            }
        }
        java.awt.Color awt = new java.awt.Color(key);
        return Color.rgb(awt.getRed(), awt.getGreen(), awt.getBlue());
    }

    public static Corner getCorner(double x, double y, double width, double height, double borderSize) {
        if (x < borderSize && x >= 0) {
            if (y < borderSize && y >= 0) {
                return Corner.TOP_LEFT;
            } else if (y >= height - borderSize && y < height) {
                return Corner.BOTTOM_LEFT;
            } else {
                return Corner.CENTER_LEFT;
            }
        } else if (x >= width - borderSize && x < width) {
            if (y < borderSize && y >= 0) {
                return Corner.TOP_RIGHT;
            } else if (y >= height - borderSize && y < height) {
                return Corner.BOTTOM_RIGHT;
            } else {
                return Corner.CENTER_RIGHT;
            }
        } else {
            if (y < borderSize && y >= 0) {
                return Corner.TOP;
            } else if (y >= height - borderSize && y < height) {
                return Corner.BOTTOM;
            } else {
                if (x >= 0 && x < width && y >= 0 && y < height) {
                    return Corner.CENTER;
                } else {
                    return Corner.OUTSIDE;
                }
            }
        }
    }

}
