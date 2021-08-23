package thito.nodeflow.internal;

import java.lang.management.*;
import java.lang.reflect.*;
import java.util.*;

public class Bootstrap {
    public Bootstrap(String[] args) {
        try {
            RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
            List<String> arguments = runtimeMxBean.getInputArguments();
            System.out.println("RUNNING JAVA "+runtimeMxBean.getVmName()+" "+runtimeMxBean.getVmVendor()+" "+runtimeMxBean.getVmVersion());
            for (String arg : arguments) {
                System.out.println(arg);
            }
            System.out.println("END");
            /**
             * We hate java modular system! Java modular system is such a pain in the ass...
             * This hacky trick might be no longer working in the future releases of Java tho...
             */
            Method method = Module.class.getDeclaredMethod("implAddExportsOrOpens", String.class, Module.class, boolean.class, boolean.class);
            method.setAccessible(true);
            Module unnamedModule = getClass().getClassLoader().getUnnamedModule();
            for (Module module : ModuleLayer.boot().modules()) {
                for (String pack : module.getPackages()) {
                    method.invoke(module, pack, unnamedModule, true, true);
                }
            }
            Main.launch(Main.class, args);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
