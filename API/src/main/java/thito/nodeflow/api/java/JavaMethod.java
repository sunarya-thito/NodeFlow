package thito.nodeflow.api.java;

import thito.nodeflow.api.bundle.java.docs.MethodJavaDoc;

public interface JavaMethod extends JavaMember {
    String getName();

    JavaType getReturnType();

    JavaField[] getArguments();

    JavaClass getDeclaringClass();

    MethodJavaDoc getJavaDoc();
}
