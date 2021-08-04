package thito.nodeflow.internal.ui;

import javafx.beans.property.*;
import thito.nodeflow.api.ui.*;

public class ColorImpl implements Color {
    private final ObjectProperty<javafx.scene.paint.Color> color = new SimpleObjectProperty<>();

    public ColorImpl(int red, int green, int blue, int alpha) {
        newColor(red, green, blue, alpha);
    }

    public ObjectProperty<javafx.scene.paint.Color> colorProperty() {
        return color;
    }

    private void newColor(int red, int green, int blue, int alpha) {
        color.set(javafx.scene.paint.Color.rgb(red, green, blue, alpha / 255d));
    }

    @Override
    public int getRed() {
        return (int) (color.get().getRed() * 255);
    }

    @Override
    public int getGreen() {
        return (int) (color.get().getGreen() * 255);
    }

    @Override
    public int getBlue() {
        return (int) (color.get().getBlue() * 255);
    }

    @Override
    public int getAlpha() {
        return (int) (color.get().getOpacity() * 255);
    }

    @Override
    public void setRed(int red) {
        newColor(red, getGreen(), getBlue(), getAlpha());
    }

    @Override
    public void setGreen(int green) {
        newColor(getRed(), green, getBlue(), getAlpha());
    }

    @Override
    public void setBlue(int blue) {
        newColor(getRed(), getGreen(), blue, getAlpha());
    }

    @Override
    public void setAlpha(int alpha) {
    }
}
