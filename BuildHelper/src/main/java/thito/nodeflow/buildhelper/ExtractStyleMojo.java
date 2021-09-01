package thito.nodeflow.buildhelper;

import com.google.gson.*;
import com.helger.commons.system.*;
import com.helger.css.*;
import com.helger.css.decl.*;
import com.helger.css.reader.*;
import com.helger.css.utils.*;
import com.helger.css.writer.*;
import org.apache.maven.plugin.*;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.*;
import org.apache.maven.project.*;

import java.awt.*;
import java.awt.color.*;
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
        Set<Named> colors = new HashSet<>();
        for (File file : files) {
            try {
                CSSReaderSettings settings = new CSSReaderSettings();
                settings.setCSSVersion(ECSSVersion.CSS30);
                CascadingStyleSheet css = CSSReader.readFromFile(file, settings);
                for (ICSSTopLevelRule rule : css.getAllRules()) {
                    if (rule instanceof CSSStyleRule) {
                        CSSStyleRule styleRule = (CSSStyleRule) rule;
                        for (CSSDeclaration dec : styleRule.getAllDeclarations()) {
                            List<ICSSExpressionMember> expressionMembers = new ArrayList<>();
                            CSSExpression expression = dec.getExpression();
                            for (int i = 0; i < expression.getMemberCount(); i++) {
                                ICSSExpressionMember mem = expression.getMemberAtIndex(i);
                                mem = replace(mem, colors);
                                expressionMembers.add(mem);
                            }
                            expression.removeAllMembers();
                            expressionMembers.forEach(expression::addMember);
                        }
                    }
                }
                CSSWriterSettings writerSettings = new CSSWriterSettings();
                writerSettings.setCSSVersion(ECSSVersion.CSS30);
                writerSettings.setQuoteURLs(true);
                CSSWriter writer = new CSSWriter(writerSettings);
                try (FileWriter fileWriter = new FileWriter(file)) {
                    writer.writeCSS(css, fileWriter);
                }
            } catch (Exception e) {
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

    private ICSSExpressionMember replace(ICSSExpressionMember member, Set<Named> colors) {
        if (member instanceof CSSExpressionMemberTermSimple) {
            String value = ((CSSExpressionMemberTermSimple) member).getValue();
            if (CSSColorHelper.isHexColorValue(value)) {
                Color color = Color.decode(value);
                String replacement = putCounter(colors, new JsonColor(color));
                ((CSSExpressionMemberTermSimple) member).setValue(replacement);
            }
        } else if (member instanceof CSSExpressionMemberFunction) {
            CSSExpressionMemberFunction function = (CSSExpressionMemberFunction) member;
            CSSExpression expression = function.getExpression();
            if (function.getFunctionName().equals("hsb") || function.getFunctionName().equals("hsba")) {
                Color rgb = Color.getHSBColor(Integer.parseInt(expression.getAllSimpleMembers().get(0).getAsCSSString()) / 360f,
                        Integer.parseInt(expression.getAllSimpleMembers().get(1).getAsCSSString().replace("%", "")) / 100f,
                        Integer.parseInt(expression.getAllSimpleMembers().get(2).getAsCSSString().replace("%", "")) / 100f);
                String replacement;
                if (expression.getAllSimpleMembers().size() > 3) {
                    replacement = putCounter(colors, new JsonColor(new Color(rgb.getRed(), rgb.getGreen(), rgb.getBlue(),
                            (int) (Double.parseDouble(expression.getAllSimpleMembers().get(3).getAsCSSString()) * 255))));
                } else {
                    replacement = putCounter(colors, new JsonColor(rgb));
                }
                return new CSSExpressionMemberTermSimple(replacement);
            } else if (function.getFunctionName().equals("rgb") || function.getFunctionName().equals("rgba")) {
                String redString = expression.getAllSimpleMembers().get(0).getAsCSSString();
                String blueString = expression.getAllSimpleMembers().get(1).getAsCSSString();
                String greenString = expression.getAllSimpleMembers().get(2).getAsCSSString();
                double red = redString.contains("%") ? Double.parseDouble(redString.replace("%", "")) / 100d * 255d :
                        Integer.parseInt(redString);
                double green = greenString.contains("%") ? Double.parseDouble(greenString.replace("%", "")) / 100d * 255d :
                        Integer.parseInt(greenString);
                double blue = blueString.contains("%") ? Double.parseDouble(blueString.replace("%", "")) / 100d * 255d :
                        Integer.parseInt(blueString);
                String replacement;
                if (expression.getAllSimpleMembers().size() > 3) {
                    String alphaString = expression.getAllSimpleMembers().get(3).getAsCSSString();
                    double alpha = Double.parseDouble(alphaString) * 255;
                    replacement = putCounter(colors, new JsonColor(new Color((int) red, (int) green, (int) blue, (int) alpha)));
                } else {
                    replacement = putCounter(colors, new JsonColor(new Color((int) red, (int) green, (int) blue)));
                }
                return new CSSExpressionMemberTermSimple(replacement);
            } else {
                List<ICSSExpressionMember> expressionMembers = new ArrayList<>();
                for (int i = 0; i < expression.getMemberCount(); i++) {
                    ICSSExpressionMember mem = expression.getMemberAtIndex(i);
                    mem = replace(mem, colors);
                    expressionMembers.add(mem);
                }
                expression.removeAllMembers();
                expressionMembers.forEach(expression::addMember);
            }
        }
        return member;
    }

    private String putCounter(Set<Named> set, JsonColor value) {
        for (Named t : set) {
            if (t.getValue().equals(value)) {
                return t.getName();
            }
        }
        int index = set.size();
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
            if (!(o instanceof JsonColor jsonColor)) return false;
            return red == jsonColor.red && green == jsonColor.green && blue == jsonColor.blue && alpha == jsonColor.alpha;
        }

        @Override
        public int hashCode() {
            return Objects.hash(red, green, blue, alpha);
        }
    }

    public static class Named {
        private String name;
        private JsonColor value;

        public Named(String name, JsonColor value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public JsonColor getValue() {
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
}
