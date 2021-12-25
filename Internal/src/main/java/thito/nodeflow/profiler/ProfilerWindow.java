package thito.nodeflow.profiler;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import thito.nodeflow.task.ScheduledTask;
import thito.nodeflow.task.TaskThread;
import thito.nodeflow.ui.StandardWindow;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.*;

public class ProfilerWindow extends StandardWindow {
    private static final int maxData = 50;
    private LineChart<Number, Number> cpuUsageChart, memoryUsageChart;
    private Map<Long, XYChart.Series<Number, Number>> threadSeriesMap = new HashMap<>();
    private Map<Long, Long> cachedTime = new HashMap<>();
    private XYChart.Series<Number, Number> usedMemorySeries, committedMemorySeries;
    private int tickCounter = 0;
    private ScheduledTask task;
    @Override
    protected void initializeWindow() {
        super.initializeWindow();
        BorderPane borderPane = new BorderPane();

        HBox centerBox = new HBox();

        VBox memBox = new VBox();
        HBox.setHgrow(memBox, Priority.ALWAYS);
        memoryUsageChart = new LineChart<>(createNumberAxis(), createNumberAxis());
        memoryUsageChart.setAnimated(false);
        usedMemorySeries = new XYChart.Series<>();
        usedMemorySeries.setName("Used Memory");
        VBox.setVgrow(memoryUsageChart, Priority.ALWAYS);
        committedMemorySeries = new XYChart.Series<>();
        committedMemorySeries.setName("Used Non-Heap Memory");
        memoryUsageChart.getData().addAll(usedMemorySeries, committedMemorySeries);
        memBox.getChildren().add(memoryUsageChart);

        VBox cpuBox = new VBox();
        HBox.setHgrow(cpuBox, Priority.ALWAYS);
        cpuUsageChart = new LineChart<>(createNumberAxis(), createNumberAxis());
        VBox.setVgrow(cpuUsageChart, Priority.ALWAYS);
        cpuUsageChart.setAnimated(false);

        cpuBox.getChildren().add(cpuUsageChart);

        centerBox.getChildren().addAll(memBox, cpuBox);

        borderPane.setCenter(centerBox);

        contentProperty().set(borderPane);

        getStage().showingProperty().addListener((obs, old, val) -> {
            if (task != null) task.cancel();
            if (val) {
                task = TaskThread.BG().schedule(this::refreshData, Duration.millis(200), Duration.millis(200));
            }
        });
    }

    private NumberAxis createNumberAxis() {
        NumberAxis numberAxis = new NumberAxis();
        numberAxis.setAutoRanging(true);
        numberAxis.setForceZeroInRange(false);
        numberAxis.setAnimated(false);
        return numberAxis;
    }

    boolean contains(long[] x, long y) {
        for (int i = 0; i < x.length; i++) if (x[i] == y) return true;
        return false;
    }
    void refreshData() {
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        long[] allThreadIds = threadMXBean.getAllThreadIds();
        for (Long thread : allThreadIds) {
            ThreadInfo info = threadMXBean.getThreadInfo(thread);
            Long cached = cachedTime.get(thread);
            XYChart.Series<Number, Number> threadSeries = threadSeriesMap.computeIfAbsent(thread, threadName -> {
                XYChart.Series<Number, Number> series = new XYChart.Series<>();
                TaskThread.UI().schedule(() -> {
                    cpuUsageChart.getData().add(series);
                });
                series.setName(info.getThreadName());
                return series;
            });
            long threadCpuTime = threadMXBean.getThreadCpuTime(thread);
            submitData(new XYChart.Data<>(tickCounter, cached == null ? 0 : threadCpuTime - cached), threadSeries);
            cachedTime.put(thread, threadCpuTime);
        }
        Iterator<Map.Entry<Long, XYChart.Series<Number, Number>>> entries = threadSeriesMap.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<Long, XYChart.Series<Number, Number>> next = entries.next();
            if (!contains(allThreadIds, next.getKey())) {
                cachedTime.remove(next.getKey());
                entries.remove();
                TaskThread.UI().schedule(() -> {
                    cpuUsageChart.getData().remove(next.getValue());
                });
            }
        }
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        submitData(new XYChart.Data<>(tickCounter, memoryMXBean.getHeapMemoryUsage().getUsed()), usedMemorySeries);
        submitData(new XYChart.Data<>(tickCounter, memoryMXBean.getNonHeapMemoryUsage().getUsed()), committedMemorySeries);
        tickCounter++;
    }
    void submitData(XYChart.Data<Number, Number> data, XYChart.Series<Number, Number> series) {
        TaskThread.UI().schedule(() -> {
            if (series.getData().isEmpty()) {
                for (int i = 0; i < maxData; i++) {
                    series.getData().add(new XYChart.Data<>(i, 0));
                }
            }
            for (int i = 0; i < maxData - 1; i++) {
                XYChart.Data<Number, Number> d = series.getData().get(i);
                XYChart.Data<Number, Number> f = series.getData().get(i + 1);
                d.setYValue(f.getYValue());
            }
            series.getData().get(maxData - 1).setYValue(data.getYValue());
        });
    }
}
