package thito.nodeflow.internal.search;

import javafx.collections.*;

public class SearchManager {
    private static SearchManager instance = new SearchManager();

    public static SearchManager getInstance() {
        return instance;
    }

    private ObservableList<SearchableContentProvider> providerList = FXCollections.observableArrayList();

    public ObservableList<SearchableContentProvider> getProviderList() {
        return providerList;
    }
}
