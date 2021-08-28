import javafx.beans.binding.*;
import javafx.beans.property.*;
import thito.nodeflow.library.language.*;
import thito.nodeflow.library.util.*;

import java.io.*;
import java.util.*;
import java.util.stream.*;

public class BoxingTest {
    public static void main(String[] args) throws Throwable {
        Language.setLanguage(new Language("en_us"));
        I18n i18n = I18n.$("file-size.B");
        i18n.set("%sB");
        LongProperty size = new SimpleLongProperty(10);
        size.addListener((obs, old, val) -> System.out.println("NEW VALUE "+val));
        StringBinding fileSize = Toolkit.formatFileSize(size);
        fileSize.addListener((obs, old, val) -> System.out.println(val));
        Bindings.when(size.greaterThanOrEqualTo(15)).then(Bindings.when(
                size.greaterThanOrEqualTo(20)
        ).then("YES").otherwise("HUH")).otherwise("NO").addListener((obs, old, val) -> System.out.println("RESULT: "+val));
        System.out.println(fileSize.get());
        size.divide(2).addListener((obs, old, val) -> System.out.println("DIVIDED "+val));
        size.set(230);
        i18n.set("%sMB");
    }

    public Object test = new Object();

    public static Set<Class> findAllClassesUsingClassLoader(String packageName) {
        InputStream stream = ClassLoader.getSystemClassLoader()
                .getResourceAsStream(packageName.replaceAll("[.]", "/"));
        System.out.println(stream);
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        return reader.lines()
                .filter(line -> line.endsWith(".class"))
                .map(line -> getClass(line, packageName))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private static Class getClass(String className, String packageName) {
        try {
            return Class.forName(packageName + "."
                    + className.substring(0, className.lastIndexOf('.')));
        } catch (ClassNotFoundException e) {
            // handle the exception
        }
        return null;
    }
}
