package thito.nodeflow.internal.search;

public interface SearchableContent {
    SearchableContentContext getContext();
    boolean isInSelection(SearchResult query);
    SearchSession search(SearchQuery query);
}
