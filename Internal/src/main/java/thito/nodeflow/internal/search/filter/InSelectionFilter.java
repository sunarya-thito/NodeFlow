package thito.nodeflow.internal.search.filter;

import thito.nodeflow.internal.search.*;

public class InSelectionFilter implements SearchFilter {
    @Override
    public boolean acceptSearch(SearchResult result) {
        return result.getContent().isInSelection(result);
    }
}
