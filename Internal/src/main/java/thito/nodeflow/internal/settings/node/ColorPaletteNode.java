package thito.nodeflow.internal.settings.node;

import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import thito.nodeflow.internal.settings.*;
import thito.nodeflow.library.ui.*;

import java.util.*;
import java.util.stream.*;

public class ColorPaletteNode extends SettingsNodePane<ColorValues> {

    public static class Factory implements SettingsNodeFactory<ColorValues> {
        @Override
        public SettingsNode<ColorValues> createNode(SettingsProperty<ColorValues> item) {
            return new ColorPaletteNode(item);
        }
    }

    private VBox box = new VBox();
    private MasonryPane paletteContainer = new MasonryPane();
    private ColorChooserPane colorChooserPane = new ColorChooserPane();
    private ObjectProperty<ColorPalette> selectedPalette = new SimpleObjectProperty<>();
    private ObservableSet<ColorPalette> changed = FXCollections.observableSet();

    public ColorPaletteNode(SettingsProperty<ColorValues> item) {
        super(item);
        hasChangedProperty().bind(Bindings.isNotEmpty(changed));
        box.setFillWidth(true);
        box.getChildren().addAll(paletteContainer, colorChooserPane);
        paletteContainer.setGap(0);
        paletteContainer.getChildren().addAll(item.get().getColorMap().entrySet().stream().map(e ->
                new ColorPalette(e.getKey(), e.getValue())).collect(Collectors.toList()));
        selectedPalette.addListener((obs, old, val) -> {
            if (old != null) {
                old.setBorder(new Border(new BorderStroke(Color.color(1, 1, 1), BorderStrokeStyle.SOLID, null, new BorderWidths(1))));
                Bindings.unbindBidirectional(colorChooserPane.currentColorProperty(), old.colorProperty());
            }
            if (val != null) {
                val.setBorder(new Border(new BorderStroke(Color.color(1, 1, 1, 1), BorderStrokeStyle.SOLID, null, new BorderWidths(3))));
                Bindings.bindBidirectional(colorChooserPane.currentColorProperty(), val.colorProperty());
            }
        });
    }

    @Override
    public Node getNode() {
        return box;
    }

    @Override
    public void apply() {
        Map<String, Color> map = new HashMap<>();
        for (Node n : paletteContainer.getChildren()) {
            ColorPalette cp = (ColorPalette) n;
            map.put(cp.name, cp.color.get());
            cp.original = cp.color.get();
        }
        changed.clear();
        ColorValues newValues = new ColorValues(map);
        getItem().set(newValues);
    }

    public class ColorPalette extends StackPane {
        private String name;
        private Color original;
        private ObjectProperty<Color> color = new SimpleObjectProperty<>();

        public ColorPalette(String name, Color col) {
            this.original = col;
            this.name = name;
            this.color.set(col);
            color.addListener((obs, old, val) -> {
                if (val.equals(original)) {
                    changed.remove(this);
                } else {
                    changed.add(this);
                }
            });
            setMinHeight(30);
            setMinWidth(30);
            Pane colored = new Pane();
            getChildren().add(colored);
            colored.backgroundProperty().bind(Bindings.createObjectBinding(() ->
                    new Background(new BackgroundFill(color.get(), null, null)), color));
            setBackground(new Background(new BackgroundImage(new Image("rsrc:Images/Transparent.png"), BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, null, BackgroundSize.DEFAULT)));
            setOnMousePressed(event -> {
                selectedPalette.set(this);
            });
            setBorder(new Border(new BorderStroke(Color.color(1, 1, 1), BorderStrokeStyle.SOLID, null, new BorderWidths(1))));
        }

        public ObjectProperty<Color> colorProperty() {
            return color;
        }
    }
}
