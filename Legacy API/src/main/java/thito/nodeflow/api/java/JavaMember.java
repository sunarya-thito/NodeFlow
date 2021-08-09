package thito.nodeflow.api.java;

import thito.nodeflow.api.bundle.java.docs.MemberJavaDoc;

public interface JavaMember {
    JavaClass getDeclaringClass();

    boolean isStatic();

    MemberJavaDoc getJavaDoc();
}
