package thito.nodeflow.internal.bundle.java.docs;

import thito.nodeflow.api.bundle.java.docs.*;

import java.lang.reflect.*;

public interface JavaDocFetcher {
    MethodJavaDoc parseMethod(Method method, String html);
    FieldJavaDoc parseField(Field field, String html);
}
