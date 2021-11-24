package thito.nodeflow.binding;

import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.collections.*;
import thito.nodeflow.task.TaskThread;

import java.lang.ref.*;
import java.util.*;

public class ThreadBinding {
    public static <T> void bind(WritableValue<T> property, ObservableValue<T> source, TaskThread thread) {
        if (property instanceof Property) {
            ((Property<T>) property).unbind();
        }
        thread.schedule(() -> {
            property.setValue(source.getValue());
            source.addListener(new WeakReferencedListener(obs -> {
                thread.schedule(() -> {
                    property.setValue(source.getValue());
                });
            }, property));
        });
    }

    public static <T> void bindContent(List<T> list, ObservableList<T> observableList, TaskThread thread) {
        if (list instanceof ObservableList) {
            ((ObservableList<T>) list).setAll(observableList);
        } else list.clear();
        observableList.addListener(new ThreadListBinding<>(list, observableList, thread));
    }

    public static <T> void unbindContent(List<T> list, ObservableList<T> observableList, TaskThread thread) {
        observableList.removeListener(new ThreadListBinding<>(list, observableList, thread));
    }

    public static class ThreadListBinding<T> implements ListChangeListener<T> {
        private WeakReference<List<T>> target;
        private ObservableList<T> observableList;
        private TaskThread thread;

        public ThreadListBinding(List<T> target, ObservableList<T> observableList, TaskThread taskThread) {
            this.target = new WeakReference<>(target);
            this.observableList = observableList;
            this.thread = taskThread;
        }

        @Override
        public void onChanged(Change<? extends T> change) {
            final List<T> list = target.get();
            if (list == null) {
                change.getList().removeListener(this);
            } else {
                synchronized (list) {
                    while (change.next()) {
                        if (change.wasPermutated()) {
                            List<? extends T> permuted = new ArrayList<>(change.getList().subList(change.getFrom(), change.getTo()));
                            thread.schedule(() -> {
                                list.subList(change.getFrom(), change.getTo()).clear();
                                list.addAll(change.getFrom(), permuted);
                            });
                        } else {
                            if (change.wasRemoved()) {
                                thread.schedule(() -> {
                                    list.subList(change.getFrom(), change.getFrom() + change.getRemovedSize()).clear();
                                });
                            }
                            if (change.wasAdded()) {
                                List<? extends T> addedSubList = new ArrayList<>(change.getAddedSubList());
                                thread.schedule(() -> {
                                    list.addAll(change.getFrom(), addedSubList);
                                });
                            }
                        }
                    }
                }
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ThreadListBinding<?> that)) return false;
            return target.get() == that.target.get() && observableList.equals(that.observableList) && thread == that.thread;
        }

        @Override
        public int hashCode() {
            return Objects.hash(target.get(), observableList, thread);
        }
    }
}
