package thito.nodeflow.internal.settings.node;

import javafx.scene.*;
import thito.nodeflow.internal.settings.*;

public class ColorPaletteNode extends SettingsNodePane<ColorValues> {
    public ColorPaletteNode(SettingsProperty<ColorValues> item) {
        super(item);
    }

    @Override
    public Node getNode() {
        return null;
    }

    @Override
    public void apply() {

    }
}
