package thito.nodeflow.internal.reference;

import thito.nodeflow.internal.util.*;

public interface DocumentReference {
    default int calculateSearchScore(String search) {
        int count = 0;
        count += Toolkit.searchScore(getDocumentTitle(), search) * 4;
        count += Toolkit.searchScore(getDocumentContent(), search);
        return count;
    }
    String getDocumentTitle();
    String getDocumentContent();
}
