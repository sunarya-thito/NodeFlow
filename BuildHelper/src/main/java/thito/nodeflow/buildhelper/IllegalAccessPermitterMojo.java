package thito.nodeflow.buildhelper;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

import java.lang.reflect.Method;

@Mojo(name = "illegal-access")
public class IllegalAccessPermitterMojo extends AbstractMojo {
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            Method method = Module.class.getDeclaredMethod("implAddExportsOrOpens", String.class, Module.class, boolean.class, boolean.class);
            method.setAccessible(true);
            Module unnamedModule = getClass().getClassLoader().getUnnamedModule();
            for (Module module : ModuleLayer.boot().modules()) {
                for (String pack : module.getPackages()) {
                    method.invoke(module, pack, unnamedModule, true, true);
                }
            }
        } catch (Throwable t) {
            throw new MojoExecutionException("Failed to do reflection", t);
        }
    }
}
