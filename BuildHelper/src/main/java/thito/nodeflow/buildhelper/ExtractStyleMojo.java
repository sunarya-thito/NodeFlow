package thito.nodeflow.buildhelper;

import com.google.gson.*;
import cz.vutbr.web.css.*;
import cz.vutbr.web.csskit.*;
import org.apache.maven.plugin.*;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.*;
import org.apache.maven.project.*;

import java.awt.Color;
import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.*;
import java.util.stream.*;

@Mojo(name = "extract-style", defaultPhase = LifecyclePhase.PACKAGE)
public class ExtractStyleMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    MavenProject project;

    @Parameter
    String sourceRoot;

    @Parameter(defaultValue = "style", required = true)
    String styleFile;

    @Parameter(defaultValue = "color-pattern-%s", required = true)
    String pattern;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        Set<File> files = new HashSet<>();
        File output = new File(project.getBuild().getOutputDirectory());
        if (sourceRoot != null && !sourceRoot.isEmpty()) {
            output = new File(output, sourceRoot);
        }
        if (output.exists()) {
            collectResources(files, output);
        } else throw new MojoExecutionException("Invalid output", new FileNotFoundException(output.getPath()));
        AtomicInteger colorCounter = new AtomicInteger();
        Set<Named> colors = new HashSet<>();
        for (File file : files) {
            try {
                StyleSheet sheet = CSSFactory.parse(file.getAbsolutePath(), "UTF-8");
                for (RuleBlock<?> ruleBlock : sheet) {
                    for (Object style : ruleBlock) {
                        if (style instanceof Declaration) {
                            List<Term> replacement = new ArrayList<>();
                            for (Term term : (Declaration) style) {
                                Object value = term.getValue();
                                if (value instanceof cz.vutbr.web.csskit.Color) {
                                    Color awt = new Color(((cz.vutbr.web.csskit.Color) value).getRed(),
                                            ((cz.vutbr.web.csskit.Color) value).getGreen(),
                                            ((cz.vutbr.web.csskit.Color) value).getBlue(),
                                            ((cz.vutbr.web.csskit.Color) value).getAlpha());
                                    String name = putCounter(colors, new JsonColor(awt), colorCounter);
                                    replacement.add(new TermString(name));
                                } else {
                                    String string = term.toString().replace(" ", "");
                                    if (string.startsWith("rgb(") && string.endsWith(")")) {
                                        string = string.substring(4, string.length() - 1);
                                        String[] split = string.split(",");
                                        if (split.length == 4) {
                                            Color awt = new Color(Integer.parseInt(split[0]),
                                                    Integer.parseInt(split[1]),
                                                    Integer.parseInt(split[2]),
                                                    (int) (Double.parseDouble(split[3]) * 255d));
                                            String name = putCounter(colors, new JsonColor(awt), colorCounter);
                                            replacement.add(new TermString(name));
                                            continue;
                                        } else if (split.length == 3) {
                                            Color awt = new Color(Integer.parseInt(split[0]),
                                                    Integer.parseInt(split[1]),
                                                    Integer.parseInt(split[2]));
                                            String name = putCounter(colors, new JsonColor(awt), colorCounter);
                                            replacement.add(new TermString(name));
                                            continue;
                                        } else throw new MojoExecutionException("Invalid color: "+term);
                                    }
                                    replacement.add(term);
                                }
                            }
                            // also making sure that we don't miss anything
                            for (int i = 0; i < ((Declaration) style).size(); i++) {
                                ((Declaration) style).set(i, replacement.get(i));
                            }
                        }
                    }
                }
                Files.writeString(file.toPath(), sheet.stream().map(String::valueOf).collect(Collectors.joining("\n")), StandardCharsets.UTF_8);
            } catch (CSSException | IOException e) {
                throw new MojoExecutionException("Failed to parse "+file, e);
            }
        }
        getLog().info("Extracted "+colors.size()+" colors!");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            Files.writeString(new File(output, styleFile + ".json").toPath(), gson.toJson(colors), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to write "+styleFile+".json", e);
        }
    }

    private String putCounter(Set<Named> set, Object value, AtomicInteger counter) {
        for (Named t : set) {
            if (t.getValue().equals(value)) {
                return t.getName();
            }
        }
        int index = counter.getAndIncrement();
        String name = String.format(pattern, index);
        set.add(new Named(name, value));
        return name;
    }

    private static void collectResources(Set<File> files, File parent) {
        if (parent.getName().endsWith(".css")) {
            files.add(parent);
        }
        File[] list = parent.listFiles();
        if (list != null) {
            for (File f : list) {
                collectResources(files, f);
            }
        }
    }

    public static class JsonColor {
        private int red, green, blue, alpha;

        public JsonColor(Color color) {
            red = color.getRed();
            green = color.getGreen();
            blue = color.getBlue();
            alpha = color.getAlpha();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof JsonColor)) return false;
            JsonColor jsonColor = (JsonColor) o;
            return red == jsonColor.red && green == jsonColor.green && blue == jsonColor.blue && alpha == jsonColor.alpha;
        }

        @Override
        public int hashCode() {
            return Objects.hash(red, green, blue, alpha);
        }
    }

    public static class Named {
        private String name;
        private Object value;

        public Named(String name, Object value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public Object getValue() {
            return value;
        }

        @Override
        public String toString() {
            return "Named{" +
                    "name='" + name + '\'' +
                    ", value=" + value +
                    '}';
        }
    }

    public static class TermString extends TermStringImpl {
        public TermString(String value) {
            super();
            setValue(value);
        }

        @Override
        public String toString() {
            return getValue();
        }
    }
}
