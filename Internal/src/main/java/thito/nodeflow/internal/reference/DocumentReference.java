package thito.nodeflow.internal.reference;

import thito.nodeflow.library.util.*;

public interface DocumentReference {
    default int calculateSearchScore(String search) {
        int count = 0;
        count += Toolkit.similarity(getDocumentTitle(), search) * 4;
        count += Toolkit.similarity(getDocumentContent(), search);
        return count;
    }
    String getDocumentTitle();
    String getDocumentContent();
}
