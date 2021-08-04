package thito.nodeflow.launcher;

import javafx.application.*;

import javax.swing.*;
import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.util.*;
import java.util.stream.*;

public class Launcher {
    private static List<Class<?>> fetchAll() throws Throwable {
        Field f = ClassLoader.class.getDeclaredField("classes");
        f.setAccessible(true);

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Vector<Class<?>> classes =  (Vector<Class<?>>) f.get(classLoader);
        return classes.stream().collect(Collectors.toList());
    }

    static void initializeApplication() throws Throwable {
        System.out.println("Loading libraries...");
        URLClassLoader apiClassLoader = new URLClassLoader(new URL[] {$("bin/NodeFlow-API.jar")}, ClassLoader.getSystemClassLoader());
        URLClassLoader internalClassLoader = new URLClassLoader(new URL[] {$("bin/NodeFlow-Internal.jar")}, apiClassLoader);
        System.out.println("Loading JavaFX thread...");
        Platform.runLater(() -> {
            System.out.println("Initializing Environment...");
            try {
                Class<?> EULA = Class.forName("thito.nodeflow.internal.EULA", true, internalClassLoader);
                // EULA CHECK
                int eula = (int) EULA.getMethod("isEulaAccepted").invoke(null);
                if (eula != 1) {
                    Object eulaWindow = EULA.newInstance();
                    EULA.getMethod("show").invoke(eulaWindow);
                    EULA.getMethod("saveEula").invoke(null);
                }

                Class<?> NodeFlowImpl = Class.forName("thito.nodeflow.internal.NodeFlowImpl", true, internalClassLoader);
                Class<?> NodeFlow = Class.forName("thito.nodeflow.api.NodeFlow", true, apiClassLoader);
                Class<?> ResourceManager = Class.forName("thito.nodeflow.api.resource.ResourceManager", true, apiClassLoader);
                Class<?> LocaleManager = Class.forName("thito.nodeflow.api.locale.LocaleManager", true, apiClassLoader);
                Class<?> UIManager = Class.forName("thito.nodeflow.api.ui.UIManager", true, apiClassLoader);
                Class<?> SplashScreen = Class.forName("thito.nodeflow.internal.ui.splash.SplashScreen", true, internalClassLoader);
                Class<?> ResourceFile = Class.forName("thito.nodeflow.api.resource.ResourceFile", true, apiClassLoader);
                Class<?> Theme = Class.forName("thito.nodeflow.api.ui.Theme", true, apiClassLoader);
                NodeFlowImpl.getMethod("initialize").invoke(null);
                Object application = NodeFlow.getMethod("getApplication").invoke(null);
//                Object localeManager = NodeFlow.getMethod("getLocaleManager").invoke(application);
                Object resourceManager = NodeFlow.getMethod("getResourceManager").invoke(application);
//                Object resource = ResourceManager.getMethod("getResource", String.class).invoke(resourceManager, "locales/en.properties");
                Object theme = ResourceManager.getMethod("getTheme", String.class).invoke(resourceManager, "Dark");
                Object uiManager = NodeFlow.getMethod("getUIManager").invoke(application);
//                LocaleManager.getMethod("loadLocale", ResourceFile)
//                        .invoke(localeManager, resource);
                UIManager.getMethod("applyTheme", Theme).invoke(uiManager, theme);
                Object splashScreen = SplashScreen.newInstance();
                SplashScreen.getMethod("show").invoke(splashScreen);
            } catch (Throwable t) {
                StringWriter output = new StringWriter();
                t.printStackTrace(new PrintWriter(output));
                t.printStackTrace();
                error("Application Crash", output.toString());
            }
        });
    }

    private static URL $(String name) throws Throwable {
        File file = new File(name).getAbsoluteFile();
        if (file.exists()) {
            System.out.println("Loading "+file+"...");
            return file.toURI().toURL();
        }
        error("Application Crash", "File not found:\n"+file);
        return null;
    }

    public static void error(String title, String message) {
        JOptionPane.showMessageDialog(null, limit(message), title, JOptionPane.ERROR_MESSAGE);
        System.exit(-1);
    }

    private static String limit(String s) {
        String[] total = s.split("\n");
        if (total.length > 20) {
            return String.join("\n", Arrays.copyOfRange(total, 0, 20));
        }
        return s;
    }
}
