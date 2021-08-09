package thito.nodeflow.library;

import java.util.*;

public interface JavaDocsParser {
    ClassMember parse(String string);
    List<ClassMember> getClasses();
    String exportGeneralized();
}
