package thito.nodeflow.buildhelper;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;

@Mojo(name = "bundle-jdk")
public class BundleJDKMojo extends AbstractMojo {
    @Parameter(name = "targetDirectory", property = "targetDirectory", required = true)
    private File targetDirectory;
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        File f = new File(System.getProperty("java.home"));
        getLog().info("Bundling JDK ("+f+")");
        targetDirectory.mkdirs();
        try {
            FileUtils.copyDirectory(f, targetDirectory);
            getLog().info("JDK has been bundled into "+targetDirectory);
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to copy directory", e);
        }
    }
}
