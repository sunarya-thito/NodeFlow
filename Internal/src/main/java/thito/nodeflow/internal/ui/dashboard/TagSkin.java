package thito.nodeflow.internal.ui.dashboard;

import javafx.scene.control.Label;
import javafx.scene.image.*;
import thito.nodeflow.internal.project.*;
import thito.nodeflow.internal.ui.Component;
import thito.nodeflow.internal.ui.Skin;
import thito.nodeflow.internal.util.*;

import java.awt.*;

public class TagSkin extends Skin {

    @Component("tag")
    Label view;

    private Tag tag;

    public TagSkin(Tag tag) {
        this.tag = tag;
    }

    @Override
    protected void onLayoutLoaded() {
        view.textProperty().bind(tag.nameProperty());
        ImageView img = new ImageView();
        img.imageProperty().addListener((obs, old, val) -> {
            if (val != null) {
                int width = (int) val.getWidth();
                int height = (int) val.getHeight();
                int[][] colors = new int[width][height];
                PixelReader reader = val.getPixelReader();
                for (int x = 0; x < width; x++) for (int y = 0; y < height; y++) colors[x][y] = reader.getArgb(x, y);
                int[] dominant = Quantize.quantizeImage(colors, 1);
                Color awt = new Color(dominant[0]);
                view.setStyle("-fx-background-color: rgb("+awt.getRed()+", "+awt.getGreen()+", "+awt.getBlue()+", 0.7)");
            }
        });
        img.imageProperty().bind(tag.iconProperty());
        view.setGraphic(img);
    }
}
