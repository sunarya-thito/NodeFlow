package thito.nodeflow.internal.search;

public interface SearchableContent {
    boolean isInSelection(SearchResult query);
    SearchSession search(SearchQuery query);
}
