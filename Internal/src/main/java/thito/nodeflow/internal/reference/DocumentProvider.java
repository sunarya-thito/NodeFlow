package thito.nodeflow.internal.reference;

import thito.nodeflow.internal.language.*;

import java.util.*;

public interface DocumentProvider {
    I18n providerName();
    List<DocumentReference> fetchDocuments(String search);
}
