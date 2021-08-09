package thito.nodeflow.library.ui.chart;

import javafx.css.*;
import javafx.scene.canvas.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import thito.nodeflow.internal.*;
import thito.nodeflow.library.ui.*;

import java.util.*;
import java.util.stream.*;

public class AnimatedLineChart extends Pane implements TickableNode {

    private static final CssMetaData<AnimatedLineChart, Color> a = Toolkit.cssMetaData("-fx-line-point-color", StyleConverter.getColorConverter(), Color.TRANSPARENT, AnimatedLineChart::backgroundLinePointColorProperty);

    private Canvas canvas = new Canvas();

    private StyleableObjectProperty<Color> backgroundLinePointColor = new SimpleStyleableObjectProperty<>(a, Color.BLACK);

    // No need to use observable property, this is tickable

    private Map<DataDisplay, Data> latestData = new HashMap<>();
    private Map<DataDisplay, List<Data>> data = new LinkedHashMap<>();
    private List<RangedData> periodData = new ArrayList<>();

    private double presetMax;

    private double gap = 30;
    private long minimumPeriodLength;

    public AnimatedLineChart() {
        getChildren().add(canvas);
        canvas.heightProperty().bind(heightProperty());
        canvas.widthProperty().bind(widthProperty());
    }

    public void setMinimumPeriodLength(long minimumPeriodLength) {
        this.minimumPeriodLength = minimumPeriodLength;
    }

    public StyleableObjectProperty<Color> backgroundLinePointColorProperty() {
        return backgroundLinePointColor;
    }

    public synchronized void pushLine(Data data) {
        latestData.put(data.getDisplay(), data);
        this.data.computeIfAbsent(data.getDisplay(), x -> new ArrayList<>());
    }

    public double getPresetMax() {
        return presetMax;
    }

    public void setPresetMax(double presetMax) {
        this.presetMax = presetMax;
    }

    public synchronized void pushPeriod(RangedData data) {
        periodData.add(data);
    }

    private double minValue(List<Data> lineData) {
        double value = 0;
        for (int i = lineData.size() - 1; i >= 0; i--) {
            value = Math.min(lineData.get(i).getValue(), value);
        }
        return value;
    }

    private double maxValue(List<Data> lineData) {
        double value = 0;
        for (int i = lineData.size() - 1; i >= 0; i--) {
            value = Math.max(lineData.get(i).getValue(), value);
        }
        return value;
    }

    private int tickTime = 0;
    private double offset;
    private double min, max, initMin, initMax;
    private long period = 40;
    @Override
    public synchronized void tick() {
        if (tickTime % period == 0) {
            data.forEach((a, ld) -> {
                ld.add(latestData.get(a));
                if (ld.size() > 100) ld.remove(0);
                initMin = Math.min(minValue(ld), initMin);
                initMax = Math.max(maxValue(ld), initMax);
            });
            initMax = Math.max(presetMax, initMax);
            data = data.entrySet().stream().sorted((a, b) -> {
                List<Data> data = a.getValue();
                List<Data> data2 = b.getValue();
                if (data.isEmpty()) return -1;
                if (data2.isEmpty()) return 1;
                return Double.compare(data2.get(data2.size()-1).getValue(), data.get(data.size()-1).getValue());
            }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));
        }
        if (tickTime % period == 0) {
            offset = gap;
        }
        if (offset > 0) {
            offset-= gap / period;
        }
        double speedMin = Math.abs((initMin - min) / 10);
        double speedMax = Math.abs((initMax - max) / 10);
        if (min < initMin) {
            min+=speedMin;
        } else if (min > initMin) {
            min-=speedMin;
        }
        if (max < initMax) {
            max+=speedMax;
        } else if (max > initMax) {
            max-=speedMax;
        }
        // Draw the background
        GraphicsContext context = canvas.getGraphicsContext2D();
        context.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        double height = getHeight();
        context.setStroke(backgroundLinePointColor.get());
        context.setLineWidth(0.5);
        for (double width = getWidth(); width >= -gap; width -= gap) {
            context.strokeLine(width + offset, 0, width + offset, height);
        }
        for (double h = 0; h < height + gap; h += gap) {
            context.strokeLine(0, h, getWidth(), h);
        }
        // Draw Lines
        data.forEach((a, lineData) -> {
            context.setLineCap(StrokeLineCap.ROUND);
            context.setLineWidth(3);
            context.setStroke(a.getColor());
            double width = getWidth();
            double safeMax = Math.max(max, 0.1);
            double safeHeight = height;
            double speed = 1;
            double lineOffset = gap * speed;
            context.beginPath();
            context.lineTo(getWidth() + gap, getHeight() + gap);
            for (int i = lineData.size() - 1; i >= 0; i--) {
                Data data = lineData.get(i);
                context.lineTo(width + offset,
                        height-((data.getValue() - min) / (safeMax - min) * safeHeight));
                width -= lineOffset;
            }
            if (!lineData.isEmpty()) {
                context.lineTo(-gap, height-((lineData.get(0).getValue() - min) / (safeMax - min) * safeHeight));
            }
            context.lineTo(-gap, height + gap);
            context.stroke();
            context.setFill(Color.color(a.getColor().getRed(), a.getColor().getGreen(), a.getColor().getBlue(), 0.3));
            context.fill();
            context.closePath();
        });
        tickTime++;
    }

}
