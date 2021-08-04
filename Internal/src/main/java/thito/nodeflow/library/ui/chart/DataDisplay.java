package thito.nodeflow.library.ui.chart;

import javafx.scene.paint.*;

public class DataDisplay {
    private String name;
    private Color color;

    public DataDisplay(String name, Color color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
