package thito.nodeflow.buildhelper;

import org.apache.maven.plugin.*;
import org.apache.maven.plugins.annotations.*;
import org.apache.maven.plugins.annotations.Mojo;
import org.jsoup.*;
import thito.nodeflow.javadoc.exporter.*;

import java.io.*;

@Mojo(name = "generate-docs")
public class GenerateDocsMojo extends AbstractMojo {

    @Parameter(required = true, property = "javaDocsUrl")
    String javaDocsUrl;

    @Parameter(required = true, property = "outputDirectory")
    File outputDirectory;
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Generating Java Docs from "+javaDocsUrl);
        getLog().info("Output Directory: "+outputDirectory);
        JD16Exporter exporter = new JD16Exporter(javaDocsUrl, url -> {
            try {
                return Jsoup.connect(url).execute().body();
            } catch (Throwable t) {
                throw new RuntimeException(t);
            }
        });
        try {
            exporter.export(outputDirectory);
        } catch (Exception e) {
            throw new MojoExecutionException("Failed to export", e);
        }
    }
}
