package thito.nodeflow.internal.ui.popup;

import eu.hansolo.medusa.*;
import javafx.scene.paint.*;
import javafx.scene.text.*;
import javafx.stage.*;
import thito.nodeflow.internal.*;
import thito.nodeflow.library.ui.decoration.popup.*;

public class AppStatsPopup extends PopupBase {
    public AppStatsPopup(Window owner) {
        super(owner);
        getStage().setAlwaysOnTop(true);
        Gauge gauge = GaugeBuilder.create()
                .skinType(Gauge.SkinType.TILE_KPI)
                .animated(true)
                .animationDuration(10)
                .backgroundPaint(Color.rgb(33, 40, 58, 0.8))
                .customFont(Font.font("Roboto"))
                .smoothing(true)
                .averageVisible(true)
                .averagingEnabled(true)
                .startFromZero(false)
//                .unit("MB") doesnt work for this type
                .customFontEnabled(true)
                .title("Memory Usage (MB)")
                .build();
        gauge.maxValueProperty().bind(Statistic.STATS.totalMemoryProperty().divide(1024 * 1024));
        gauge.valueProperty().bind(Statistic.STATS.usedMemoryProperty().divide(1024 * 1024));
        getRoot().getChildren().add(gauge);
    }
}
