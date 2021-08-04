package thito.nodeflow.internal.ui.resourcemonitor;

import com.sun.management.OperatingSystemMXBean;
import javafx.beans.property.*;
import javafx.scene.paint.*;
import thito.nodeflow.library.ui.*;
import thito.nodeflow.library.ui.chart.*;
import thito.nodeflow.library.ui.layout.*;

import java.lang.management.*;

public class ResourceMonitorUI extends UIComponent implements TickableNode {

    @Component("chart")
    private final ObjectProperty<AnimatedLineChart> chart = new SimpleObjectProperty<>();

//    @Component("task-list")
//    private final ObjectProperty<VBox> tasks = new SimpleObjectProperty<>();

    private final DataDisplay ramDisplay = new DataDisplay("RAM", Color.color(1, 0.3, 0.3));
    private final DataDisplay cpuDisplay = new DataDisplay("CPU", Color.color(0.4, 0.4, 1));
    private final DataDisplay overallCpuDisplay = new DataDisplay("System-CPU", Color.TEAL);

    private final OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(
            OperatingSystemMXBean.class);

    private ResourceMonitorWindow window;

    public ResourceMonitorUI(ResourceMonitorWindow resourceMonitorWindow) {
        window = resourceMonitorWindow;
        setLayout(Layout.loadLayout("ResourceMonitorUI"));
    }

    @Override
    protected void onLayoutReady() {
        chart.get().setPresetMax(1);
//        MappedListBinding.bind(tasks.get().getChildren(), window.getHistoryTask(), tsk -> {
//            Label label = new Label(tsk);
//            label.getStyleClass().add("task-item");
//            return label;
//        });
    }

    private int tickTime = 0;
    @Override
    public void tick() {
        if (tickTime % (1000 / 16) == 0) {
            AnimatedLineChart chart = this.chart.get();
            if (chart != null) {
                double ram = (Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory()) / (double) Runtime.getRuntime().totalMemory();
                double cpu = osBean.getProcessCpuLoad();
                chart.pushLine(new Data(ramDisplay, ram, System.currentTimeMillis()));
                chart.pushLine(new Data(cpuDisplay, cpu, System.currentTimeMillis()));
                chart.pushLine(new Data(overallCpuDisplay, osBean.getSystemCpuLoad(), System.currentTimeMillis()));
            }
        }
        tickTime++;
    }
}
