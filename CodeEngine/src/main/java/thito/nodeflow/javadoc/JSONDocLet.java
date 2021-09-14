package thito.nodeflow.javadoc;

import jdk.javadoc.doclet.*;

import javax.lang.model.*;
import java.util.*;

public class JSONDocLet implements Doclet {
    @Override
    public void init(Locale locale, Reporter reporter) {
    }

    @Override
    public String getName() {
        return "JSONDocLet";
    }

    @Override
    public Set<? extends Option> getSupportedOptions() {
        return Collections.emptySet();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }

    @Override
    public boolean run(DocletEnvironment environment) {
        return true;
    }
}
