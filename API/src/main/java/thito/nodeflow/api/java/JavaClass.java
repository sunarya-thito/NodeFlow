package thito.nodeflow.api.java;

import thito.nodeflow.api.bundle.java.JavaBundle;
import thito.nodeflow.api.bundle.java.docs.ClassJavaDoc;

import java.util.List;

public interface JavaClass extends JavaType, JavaMember {
    List<JavaField> getFields();

    List<JavaMethod> getMethods();

    List<JavaClass> getInnerClasses();

    JavaBundle getBundle();

    ClassJavaDoc getJavaDoc();
}
