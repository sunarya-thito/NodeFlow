package thito.nodeflow.internal.event;

import thito.nodeflow.api.event.*;
import thito.nodeflow.internal.*;

import java.lang.ref.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.*;

public class EventManagerImpl implements EventManager {
    private Set<BakedListener> listeners = ConcurrentHashMap.newKeySet();

    @Override
    public <T extends Event> T callEvent(T event) {
        Iterator<BakedListener> listeners = this.listeners.iterator();
        while (listeners.hasNext()) {
            BakedListener bakedListener = listeners.next();
            if (bakedListener.getListener() == null) {
                listeners.remove();
                continue;
            }
            bakedListener.dispatch(event);
        }
        return event;
    }

    @Override
    public void registerWeakListener(Listener listener) {
        listeners.add(new WeakBakedListener(listener));
    }

    @Override
    public void registerListener(Listener listener) {
        listeners.add(new StandardBakedListener(listener));
    }

    @Override
    public void unregisterListener(Listener listener) {
        listeners.removeIf(listenerReference -> {
            Listener x = listenerReference.getListener();
            return x == null || x == listener;
        });
    }

    public interface BakedListener {
        Listener getListener();
        void dispatch(Event event);
    }

    public class StandardBakedListener implements BakedListener {
        private Listener listener;
        private Method[] methods;

        public StandardBakedListener(Listener listener) {
            this.listener = listener;
            methods = listener.getClass().getDeclaredMethods();
            for (Method method : methods) method.setAccessible(true);
        }

        @Override
        public Listener getListener() {
            return listener;
        }

        @Override
        public void dispatch(Event event) {
            for (Method method : methods) {
                if (method.getParameterCount() == 1) {
                    Class<?> type = method.getParameterTypes()[0];
                    if (type.isInstance(event)) {
                        Toolkit.printErrorLater(() -> {
                            method.invoke(listener, event);
                        });
                    }
                }
            }
        }
    }

    public class WeakBakedListener implements BakedListener {
        private WeakReference<Listener> listener;
        private Method[] methods;

        public WeakBakedListener(Listener listener) {
            this.listener = new WeakReference<>(listener);
            methods = listener.getClass().getDeclaredMethods();
            for (Method method : methods) method.setAccessible(true);
        }

        @Override
        public Listener getListener() {
            return listener.get();
        }

        @Override
        public void dispatch(Event event) {
            for (Method method : methods) {
                if (method.getParameterCount() == 1) {
                    Class<?> type = method.getParameterTypes()[0];
                    if (type.isInstance(event)) {
                        Toolkit.printErrorLater(() -> {
                            method.invoke(listener, event);
                        });
                    }
                }
            }
        }
    }
}
