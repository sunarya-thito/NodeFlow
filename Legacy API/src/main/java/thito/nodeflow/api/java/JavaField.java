package thito.nodeflow.api.java;

import thito.nodeflow.api.bundle.java.docs.FieldJavaDoc;

public interface JavaField extends JavaMember {
    String getName();

    JavaType getType();

    FieldJavaDoc getJavaDoc();
}
