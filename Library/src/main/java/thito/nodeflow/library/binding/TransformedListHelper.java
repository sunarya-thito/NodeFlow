package thito.nodeflow.library.binding;

import javafx.collections.transformation.*;

import java.util.*;
import java.util.function.*;

public class TransformedListHelper {

    public static <T> void reFilter(FilteredList<T> filteredList) {
        Predicate<? super T> oldPredicate = filteredList.getPredicate();
        if (oldPredicate instanceof PredicateWrapper) {
            oldPredicate = ((PredicateWrapper<? super T>) oldPredicate).predicate;
        }
        filteredList.setPredicate(new PredicateWrapper<>(oldPredicate));
    }

    public static <T> void reSort(SortedList<T> sortedList) {
        Comparator<? super T> oldComparator = sortedList.getComparator();
        if (oldComparator instanceof ComparatorWrapper) {
            oldComparator = ((ComparatorWrapper<? super T>) oldComparator).comparator;
        }
        sortedList.setComparator(new ComparatorWrapper<>(oldComparator));
    }

    public static class ComparatorWrapper<T> implements Comparator<T> {
        private Comparator<T> comparator;

        public ComparatorWrapper(Comparator<T> comparator) {
            this.comparator = comparator;
        }

        @Override
        public int compare(T o1, T o2) {
            return comparator.compare(o1, o2);
        }
    }

    public static class PredicateWrapper<T> implements Predicate<T> {
        private Predicate<T> predicate;

        public PredicateWrapper(Predicate<T> predicate) {
            this.predicate = predicate;
        }

        @Override
        public boolean test(T t) {
            return predicate.test(t);
        }
    }
}
