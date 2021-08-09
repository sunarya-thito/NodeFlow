package thito.nodeflow.launcher;

import javafx.application.*;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.geometry.Insets;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.Label;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.*;
import mslinks.*;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.security.*;
import java.text.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.zip.*;

public class Main extends Application {

    public static File ROOT = new File("").getAbsoluteFile();
    public static void main(String[] args) throws Throwable {
        File system = checkSystem();
        if (!system.getName().endsWith(new String(new byte[] {(byte) '.', (byte) 'e', (byte) 'x', (byte) 'e'}))) {
            throw new Error();
        }
        System.out.println(
                        "███╗   ██╗ ██████╗ ██████╗ ███████╗███████╗██╗      ██████╗ ██╗    ██╗\n" +
                        "████╗  ██║██╔═══██╗██╔══██╗██╔════╝██╔════╝██║     ██╔═══██╗██║    ██║\n" +
                        "██╔██╗ ██║██║   ██║██║  ██║█████╗  █████╗  ██║     ██║   ██║██║ █╗ ██║\n" +
                        "██║╚██╗██║██║   ██║██║  ██║██╔══╝  ██╔══╝  ██║     ██║   ██║██║███╗██║\n" +
                        "██║ ╚████║╚██████╔╝██████╔╝███████╗██║     ███████╗╚██████╔╝╚███╔███╔╝\n" +
                        "╚═╝  ╚═══╝ ╚═════╝ ╚═════╝ ╚══════╝╚═╝     ╚══════╝ ╚═════╝  ╚══╝╚══╝ \n" +
                        "                                                                      "
        );
        if (System.getProperty("nodeflow.branch", "master").equals("beta")) {
            System.out.println("( YOU ARE IN BETA MODE )");
            System.out.println("THE BETA SOFTWARE LICENSED HEREUNDER MAY CONTAIN DEFECTS AND A PRIMARY PURPOSE OF THIS BETA TESTING LICENSE, FOR WHICH NO FEES HAVE BEEN CHARGED OR ARE DUE FROM LICENSEE, IS TO OBTAIN FEEDBACK ON SOFTWARE PERFORMANCE AND THE IDENTIFICATION OF DEFECTS. LICENSEE IS ADVISED TO SAFEGUARD IMPORTANT DATA, TO USE CAUTION AND NOT TO RELY IN ANY WAY ON THE CORRECT FUNCTIONING OR PERFORMANCE OF THE SOFTWARE AND/OR ACCOMPANYING MATERIALS.");
        }
        System.out.println("Starting application...");
        System.setProperty("prism.lcdtext", "false");
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "[%1$tF %1$tT] [%4$s] %5$s %n");
        File logFile = new File("logs/latest.log");
        if (logFile.exists()) {
            int count = 0;
            File target;
            String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date(logFile.lastModified()));
            while ((target = new File("logs/log "+date+(count > 0 ? " ("+count+").zip" : ".zip"))).exists()) count++;
            try (ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(target)); FileInputStream fileInputStream = new FileInputStream(logFile)) {
                zipOutputStream.putNextEntry(new ZipEntry("log.txt"));
                int len;
                byte[] buff = new byte[1024 * 8];
                while ((len = fileInputStream.read(buff, 0, buff.length)) != -1) {
                    zipOutputStream.write(buff, 0, len);
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Throwable t) {
        }
        try {
            new File("logs").mkdirs();
            OutputStream outputStream = new FileOutputStream(logFile);
            PrintStream old = System.out;
            System.setOut(new PrintStream(new OutputStream() {

                @Override
                public void write(int b) throws IOException {
                    old.write(b);
                    outputStream.write(b);
                }

                @Override
                public void write(byte[] b) throws IOException {
                    old.write(b);
                    outputStream.write(b);
                }

                @Override
                public void write(byte[] b, int off, int len) throws IOException {
                    old.write(b, off, len);
                    outputStream.write(b, off, len);
                }

                @Override
                public void flush() throws IOException {
                    old.flush();
                    outputStream.flush();
                }

                @Override
                public void close() throws IOException {
                    old.close();
                    outputStream.close();
                }

            }, false, "UTF-8"));
            System.setErr(System.out);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        if (System.getProperty("nodeflow.skipupdate", "false").equals("true")) {
            service.execute(() -> {
//                new JFXPanel();
                launchApp();
            });
            return;
        }
        try {
            launch(args);
        } catch (Throwable t) {
            StringWriter output = new StringWriter();
            t.printStackTrace(new PrintWriter(output));
            Launcher.error("Application Crash", output.toString());
        }
    }

    private static ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
    private BooleanProperty showUpdate = new SimpleBooleanProperty();
    private DoubleProperty globalBar, localBar;
    private StringProperty globalStatus, localStatus;

    public static File checkSystem() throws URISyntaxException {
        return new File(toURI(getLocation(getCodeSource(getProtectionDomain(mainClass())))));
    }

    public static URI toURI(URL url) throws URISyntaxException {
        return url.toURI();
    }

    public static URL getLocation(CodeSource codeSource) {
        return codeSource.getLocation();
    }

    public static CodeSource getCodeSource(ProtectionDomain protectionDomain) {
        return protectionDomain.getCodeSource();
    }

    public static ProtectionDomain getProtectionDomain(Class<?> clazz) {
        return clazz.getProtectionDomain();
    }

    public static Class<?> mainClass() {
        return Main.class;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.onCloseRequestProperty().set(event -> {
            System.exit(0);
        });

        BorderPane root = new BorderPane();

        VBox box = new VBox();
        box.setFillWidth(true);
        root.setCenter(box);
        box.setPadding(new Insets(10, 10, 10, 10));
        box.setSpacing(10);

        Label status = new Label("Checking for updates");
        globalStatus = status.textProperty();
        ProgressBar bar = new ProgressBar(ProgressBar.INDETERMINATE_PROGRESS);
        globalBar = bar.progressProperty();
        VBox.setVgrow(bar, Priority.ALWAYS);
        bar.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        VBox global = new VBox(status, bar);
        VBox.setVgrow(global, Priority.ALWAYS);
        global.setSpacing(5);
        global.setFillWidth(true);
        box.getChildren().add(global);

        Label local = new Label("Preparing...");
        localStatus = local.textProperty();
        ProgressBar localBar = new ProgressBar(ProgressIndicator.INDETERMINATE_PROGRESS);
        this.localBar = localBar.progressProperty();
        VBox.setVgrow(localBar, Priority.ALWAYS);
        localBar.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        VBox localBox = new VBox(local, localBar);
        VBox.setVgrow(localBox, Priority.ALWAYS);
        localBox.setSpacing(5);
        localBox.setFillWidth(true);

        Scene scene = new Scene(root, 400, 120); // 120 for both, 80 for single
        primaryStage.getIcons().add(new Image("favicon.png"));
        primaryStage.setTitle("NodeFlow Updater");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.setHeight(100);
        primaryStage.show();
        centerOnScreen(primaryStage);

        showUpdate.addListener((obs, old, val) -> {
            if (val) {
                primaryStage.setHeight(120);
                primaryStage.sizeToScene();
                box.getChildren().add(localBox);
            } else {
                primaryStage.setHeight(80);
                box.getChildren().remove(localBox);
            }
        });

        service.execute(() -> {
            try {
                Updater.Branch branch = Updater.getBranch(System.getProperty("nodeflow.branch", "master"));
                List<Updater.Resource> update = Updater.filterResources(branch);
                int count = 0;
                for (Updater.Resource resource : update) {
                    File target = new File(ROOT, resource.path);
                    if (!target.exists()) {
                        System.out.println(target+" doesn't exists!");
                        count++;
                    }
                }
                if (count == update.size() && count > 0) {
                    // INSTALL
                    Platform.runLater(() -> {
                        DirectoryChooser chooser = new DirectoryChooser();
                        chooser.setTitle("Install Target Directory");
                        File result = chooser.showDialog(primaryStage);
                        if (result == null || !result.exists()) {
                            System.exit(1223); // The operation was canceled by the user
                            return;
                        }
                        ROOT = new File(result, "NodeFlow");
                        service.execute(() -> {
                            try {
                                File file = new File(Main.class.getProtectionDomain().getCodeSource().getLocation()
                                        .toURI());
                                File target;
                                ROOT.mkdirs();
                                Files.copy(file.toPath(), (target = new File(ROOT, file.getName())).toPath(), StandardCopyOption.REPLACE_EXISTING);
                                System.out.println("Creating desktop shortcut: "+target);
                                String name = file.getName().replace(".exe", "");
                                ShellLink.createLink(target.getAbsolutePath(), new File(System.getProperty("user.home"), "Desktop/"+name+".lnk").getAbsolutePath());
                                update(primaryStage, update, () -> {
                                    if (target.getAbsolutePath().equals(file.getAbsolutePath())) {
                                        System.out.println("Launching program...");
                                        launchApp();
                                    } else {
                                        try {
                                            Desktop.getDesktop().open(ROOT);
                                            System.exit(0);
                                        } catch (Throwable e) {
                                            e.printStackTrace();
                                            System.exit(1);
                                        }
                                    }
                                });
                            } catch (Throwable t) {
                                t.printStackTrace();
                                System.exit(1);
                            }
                        });
                    });
                    return;
                }
                update(primaryStage, update, () -> launchApp());
            } catch (Throwable t) {
                StringWriter output = new StringWriter();
                t.printStackTrace(new PrintWriter(output));
                t.printStackTrace();
                Launcher.error("Failed to check update", output.toString());
            }
        });

    }

    private void update(Stage primaryStage, List<Updater.Resource> update, Runnable substitute) {
        System.out.println("Preparing for update");
        Platform.runLater(() -> {
            if (!update.isEmpty()) {
                System.out.println("Show update: true");
                showUpdate.set(true);
            }
            service.execute(() -> {
                AtomicInteger doneCount = new AtomicInteger();
                for (Updater.Resource resource : update) {
                    service.execute(() -> {
                        try {
                            Platform.runLater(() -> {
                                globalStatus.set("Downloading "+resource.path+" ("+(doneCount.get()+1)+" out of "+ update.size()+")");
                            });
                            pushDownload(resource);
                            Platform.runLater(() -> {
                                doneCount.set(doneCount.get() + 1);
                                globalBar.set((double) doneCount.get() / (double) update.size());
                            });
                        } catch (Throwable t) {
                            StringWriter output = new StringWriter();
                            t.printStackTrace(new PrintWriter(output));
                            t.printStackTrace();
                            Launcher.error("Failed to download", output.toString());
                        }
                    });
                }
                service.execute(() -> {
                    Platform.runLater(() -> {
                        showUpdate.set(false);
                        primaryStage.close();
                    });
                    substitute.run();
                });
            });
        });
    }

    private static void launchApp() {
        try {
            Launcher.initializeApplication();
        } catch (Throwable t) {
            StringWriter output = new StringWriter();
            t.printStackTrace(new PrintWriter(output));
            t.printStackTrace();
            Launcher.error("Application Crash", output.toString());
        }
    }

    public void pushDownload(Updater.Resource resource) throws Throwable {
        URL url = new URL(resource.getDownloadURL());
        Platform.runLater(() -> {
            localStatus.set("Connecting...");
        });
        try (InputStream inputStream = url.openStream()) {
            File target = new File(ROOT, resource.path);
            File parent = target.getParentFile();
            if (parent != null) parent.mkdirs();
            try (FileOutputStream outputStream = new FileOutputStream(target)) {
                long total = 0;
                int len;
                byte[] buff = new byte[1024 * 8];
                while ((len = inputStream.read(buff, 0, buff.length)) != -1) {
                    outputStream.write(buff, 0, len);
                    total+=len;
                    double progression = (double) total / (double) resource.size;
                    System.out.println("Downloading "+resource.path+" ("+(progression * 100)+"%)");
                    Platform.runLater(() -> {
                        localBar.set(progression);
                        localStatus.set("Downloading "+(int)(localBar.get() * 100)+"%");
                    });
                }
            } catch (Throwable e) {} // prevent getting the link leaked
        }
    }

    public static void centerOnScreen(Stage stage) {
        ObservableList<Screen> screen = Screen.getScreensForRectangle(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight());
        if (screen != null && screen.size() > 0) {
            float CENTER_ON_SCREEN_X_FRACTION = 1.0f / 2;
            float CENTER_ON_SCREEN_Y_FRACTION = 1.0f / 2;
            Screen main = screen.get(0);
            Rectangle2D bounds = main.getVisualBounds();
            double centerX = bounds.getMinX() + (bounds.getWidth() - stage.getWidth())
                    * CENTER_ON_SCREEN_X_FRACTION;
            double centerY = bounds.getMinY() + (bounds.getHeight() - stage.getHeight())
                    * CENTER_ON_SCREEN_Y_FRACTION;
            stage.setX(centerX);
            stage.setY(centerY);
        }
    }

}
