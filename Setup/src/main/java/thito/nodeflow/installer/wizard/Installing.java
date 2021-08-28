package thito.nodeflow.installer.wizard;

import javafx.animation.*;
import javafx.application.*;
import javafx.beans.property.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.*;
import thito.nodeflow.installer.*;

import java.io.*;
import java.math.*;
import java.net.*;
import java.util.*;

public class Installing extends Wizard {

    /**
     * The number of bytes in a kilobyte.
     */
    public static final long ONE_KB = 1024;

    /**
     * The number of bytes in a kilobyte.
     *
     * @since 2.4
     */
    public static final BigInteger ONE_KB_BI = BigInteger.valueOf(ONE_KB);

    /**
     * The number of bytes in a megabyte.
     */
    public static final long ONE_MB = ONE_KB * ONE_KB;

    /**
     * The number of bytes in a megabyte.
     *
     * @since 2.4
     */
    public static final BigInteger ONE_MB_BI = ONE_KB_BI.multiply(ONE_KB_BI);

    /**
     * The number of bytes in a gigabyte.
     */
    public static final long ONE_GB = ONE_KB * ONE_MB;

    /**
     * The number of bytes in a gigabyte.
     *
     * @since 2.4
     */
    public static final BigInteger ONE_GB_BI = ONE_KB_BI.multiply(ONE_MB_BI);

    /**
     * The number of bytes in a terabyte.
     */
    public static final long ONE_TB = ONE_KB * ONE_GB;

    /**
     * The number of bytes in a terabyte.
     *
     * @since 2.4
     */
    public static final BigInteger ONE_TB_BI = ONE_KB_BI.multiply(ONE_GB_BI);

    /**
     * The number of bytes in a petabyte.
     */
    public static final long ONE_PB = ONE_KB * ONE_TB;

    /**
     * The number of bytes in a petabyte.
     *
     * @since 2.4
     */
    public static final BigInteger ONE_PB_BI = ONE_KB_BI.multiply(ONE_TB_BI);

    /**
     * The number of bytes in an exabyte.
     */
    public static final long ONE_EB = ONE_KB * ONE_PB;

    /**
     * The number of bytes in an exabyte.
     *
     * @since 2.4
     */
    public static final BigInteger ONE_EB_BI = ONE_KB_BI.multiply(ONE_PB_BI);

    /**
     * The number of bytes in a zettabyte.
     */
    public static final BigInteger ONE_ZB = BigInteger.valueOf(ONE_KB).multiply(BigInteger.valueOf(ONE_EB));

    /**
     * The number of bytes in a yottabyte.
     */
    public static final BigInteger ONE_YB = ONE_KB_BI.multiply(ONE_ZB);

    public static String byteCountToDisplaySize(long size) {
        return byteCountToDisplaySize(new BigInteger(Long.toString(size)));
    }

    public static String formatTime(long time) {
        if (time == 0) return "0 seconds";
        String display = "";
        long seconds = time / 1000 % 60;
        long minutes = time / 1000 / 60 % 60;
        long hours = time / 1000 / 60 / 60 % 24;
        long days = time / 1000 / 60 / 60 / 24;
        if (days > 0) {
            display += days + "d ";
        }
        if (hours > 0) {
            display += hours + "h ";
        }
        if (minutes > 0) {
            display += minutes + "m ";
        }
        if (seconds > 0) {
            display += seconds + "s";
        }
        return display;
    }

    public static String byteCountToDisplaySize(final BigInteger size) {
        final String displaySize;

        if (size.divide(ONE_EB_BI).compareTo(BigInteger.ZERO) > 0) {
            displaySize = size.divide(ONE_EB_BI) + " EB";
        } else if (size.divide(ONE_PB_BI).compareTo(BigInteger.ZERO) > 0) {
            displaySize = size.divide(ONE_PB_BI) + " PB";
        } else if (size.divide(ONE_TB_BI).compareTo(BigInteger.ZERO) > 0) {
            displaySize = size.divide(ONE_TB_BI) + " TB";
        } else if (size.divide(ONE_GB_BI).compareTo(BigInteger.ZERO) > 0) {
            displaySize = size.divide(ONE_GB_BI) + " GB";
        } else if (size.divide(ONE_MB_BI).compareTo(BigInteger.ZERO) > 0) {
            displaySize = size.divide(ONE_MB_BI) + " MB";
        } else if (size.divide(ONE_KB_BI).compareTo(BigInteger.ZERO) > 0) {
            displaySize = size.divide(ONE_KB_BI) + " KB";
        } else {
            displaySize = size + " bytes";
        }
        return displaySize;
    }

    public static Throwable InstallationError;
    private boolean start;
    private boolean stopped;
    private long totalSize;
    private long lastDownloaded = 0;
    private long lagSpeed = 0;
    private LongProperty globalSizeDownloaded = new SimpleLongProperty();
    private Label gLabel;
    private Label lLabel;
    private ProgressBar gProgress;
    private ProgressBar lProgress;
    private int count;
    private long time;
    public Installing() {
        titleProperty().set("Installing");

        gLabel = new Label("Connecting...");
        gProgress = new ProgressBar(-1);
        VBox global = new VBox(gLabel, gProgress);
        global.setSpacing(6);

        lLabel = new Label("Connecting...");
        lProgress = new ProgressBar(-1);
        VBox local = new VBox(lLabel, lProgress);
        local.setSpacing(6);

        Label status = new Label("");

        getChildren().addAll(global, local, status);
        setSpacing(18);

        disableBackProperty().set(true);
        disableNextProperty().set(true);

        globalSizeDownloaded.addListener((obs, old, val) -> {
            gProgress.setProgress(val.doubleValue() / ((double) totalSize));
        });

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            if (lastDownloaded <= 0) {
                time += 1000 * 60 * 60;
            } else {
                time = ((totalSize - globalSizeDownloaded.get()) / lastDownloaded) * 1000;
            }
            String estimated = formatTime(time);
            status.setText("Download Speed: "+byteCountToDisplaySize(lastDownloaded)+"/s\nEstimated Time: "+estimated+"\nComputation Time: "+lagSpeed+"ms");
            lastDownloaded = 0;
        }));
        timeline.setCycleCount(-1);
        timeline.play();

        activeProperty().addListener((obs, old, val) -> {
            if (val) {
                begin();
            } else {
                stop();
            }
        });
    }

    private void stop() {
        start = false;
        stopped = true;
    }

    private void begin() {
        if (start) return;
        stopped = false;
        start = true;
        Main.THREAD_POOL.execute(() -> {
            try {
                Updater.Branch branch = Updater.getBranch(System.getProperty("nodeflow.branch", "master"));
                List<Updater.Resource> filtered = Updater.filterResources(branch);
                for (int i = 0; i < filtered.size(); i++) totalSize += filtered.get(i).size;
                iterateNext(filtered.iterator(), filtered.size());
            } catch (Throwable throwable) {
                start = false;
                InstallationError = throwable;
                Platform.runLater(() -> {
                    Main.invokeNextStep();
                });
            }
        });
    }

    private void iterateNext(Iterator<Updater.Resource> resourceIterator, int total) throws Throwable {
        if (resourceIterator.hasNext()) {
            Updater.Resource next = resourceIterator.next();
            if (start) {
                count++;
                URL url = new URL(next.getDownloadURL());
                File output = new File(Main.INSTALLATION_DIR, next.path);
                long size = next.size;
                long downloaded = 0;
                Platform.runLater(() -> {
                    lLabel.setText("Downloading "+next.path+"...");
                    gLabel.setText("Downloading "+count+" out of "+total);
                });
                File parent = output.getParentFile();
                if (parent != null) {
                    parent.mkdirs();
                }
                try (InputStream inputStream = url.openStream(); OutputStream outputStream = new FileOutputStream(output)) {
                    byte[] buff = new byte[1024 * 8];
                    int len;
                    while (true) {
                        long time = System.currentTimeMillis();
                        if ((len = inputStream.read(buff, 0, buff.length)) == -1) break;
                        if (stopped) {
                            return;
                        }
                        outputStream.write(buff, 0, len);
                        downloaded += len;
                        lastDownloaded += len;
                        long finalDownloaded = downloaded;
                        int finalLen = len;
                        Platform.runLater(() -> {
                            globalSizeDownloaded.set(globalSizeDownloaded.get() + finalLen);
                            lProgress.setProgress(Math.min(1, finalDownloaded / ((double) size)));
                        });
                        lagSpeed = System.currentTimeMillis() - time;
                    }
                }
                iterateNext(resourceIterator, total);
            }
        } else {
           if (!stopped) {
               Platform.runLater(() -> {
                   Main.invokeNextStep();
               });
           }
        }
    }
}
