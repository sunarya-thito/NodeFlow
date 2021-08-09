package thito.nodeflow.api.ui;

import thito.nodeflow.api.NodeFlow;

public interface Color {
    int MIN = 0;
    int MAX = 255;

    static Color color(int red, int green, int blue) {
        return color(red, green, blue, MAX);
    }

    static Color color(int red, int green, int blue, int alpha) {
        return NodeFlow.getApplication().getUIManager().color(red, green, blue, alpha);
    }

    int getRed();

    void setRed(int red);

    int getGreen();

    void setGreen(int green);

    int getBlue();

    void setBlue(int blue);

    int getAlpha();

    void setAlpha(int alpha);
}
