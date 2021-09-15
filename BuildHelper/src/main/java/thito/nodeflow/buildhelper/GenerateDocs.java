package thito.nodeflow.buildhelper;

import org.apache.maven.plugin.*;
import org.apache.maven.plugins.annotations.*;
import org.apache.maven.plugins.annotations.Mojo;
import org.jsoup.*;
import thito.nodeflow.javadoc.exporter.*;

import java.io.*;

@Mojo(name = "generate-docs")
public class GenerateDocs extends AbstractMojo {
    @Parameter
    String javaDocsUrl;

    @Parameter
    File outputDirectory;
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        JD16Exporter exporter = new JD16Exporter(javaDocsUrl, url -> {
            try {
                return Jsoup.connect(url).execute().body();
            } catch (Throwable t) {
                throw new RuntimeException(t);
            }
        });
        try {
            exporter.export(outputDirectory);
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to export", e);
        }
    }
}
