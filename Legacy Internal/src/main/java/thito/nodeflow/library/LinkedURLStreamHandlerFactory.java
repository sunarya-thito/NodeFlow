package thito.nodeflow.library;

import java.lang.reflect.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class LinkedURLStreamHandlerFactory implements URLStreamHandlerFactory {

    private URLStreamHandlerFactory previousFactory;
    private final URLStreamHandlerFactory nextFactory;

    private static final Field factoryField;
    private static final Object streamHandlerLock;
    private static final Hashtable<String,URLStreamHandler> handlers;
    private static final Hashtable<URLStreamHandlerFactory, Set<URLStreamHandler>> cached = new Hashtable<>();
    static {
        try {
            factoryField = URL.class.getDeclaredField("factory");
            factoryField.setAccessible(true);
            Field streamHandlerLockField = URL.class.getDeclaredField("streamHandlerLock");
            streamHandlerLockField.setAccessible(true);
            streamHandlerLock = streamHandlerLockField.get(null);
            Field handlersField = URL.class.getDeclaredField("handlers");
            handlersField.setAccessible(true);
            handlers = (Hashtable<String, URLStreamHandler>) handlersField.get(null);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }
    private static void _setFactory(URLStreamHandlerFactory factory) {
        try {
            factoryField.set(null, factory);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }
    private static URLStreamHandlerFactory _getFactory() {
        try {
            return (URLStreamHandlerFactory) factoryField.get(null);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }
    private static void clearCache(URLStreamHandlerFactory factory) {
        Set<URLStreamHandler> factoryHandlers = cached.get(factory);
        if (factoryHandlers != null) {
            handlers.values().removeAll(factoryHandlers);
        }
    }

    public static void registerURLStreamHandlerFactory(URLStreamHandlerFactory factory) {
        synchronized (streamHandlerLock) {
            _setFactory(new LinkedURLStreamHandlerFactory(_getFactory(), factory));
        }
    }

    public static void unregisterURLStreamHandlerFactory(URLStreamHandlerFactory factory) {
        synchronized (streamHandlerLock) {
            URLStreamHandlerFactory next = null;
            URLStreamHandlerFactory current = _getFactory();
            while (current instanceof LinkedURLStreamHandlerFactory) {
                if (((LinkedURLStreamHandlerFactory) current).nextFactory == factory) {
                    clearCache(factory);
                    ((LinkedURLStreamHandlerFactory) current).previousFactory = next;
                    break;
                }
                next = current;
                current = ((LinkedURLStreamHandlerFactory) current).previousFactory;
            }
        }
    }

    private LinkedURLStreamHandlerFactory(URLStreamHandlerFactory previousFactory, URLStreamHandlerFactory nextFactory) {
        this.previousFactory = previousFactory;
        this.nextFactory = nextFactory;
    }

    @Override
    public URLStreamHandler createURLStreamHandler(String protocol) {
        if (nextFactory != null) {
            URLStreamHandler nextHandler = nextFactory.createURLStreamHandler(protocol);
            if (nextHandler != null) {
                cached.computeIfAbsent(nextFactory, x -> ConcurrentHashMap.newKeySet()).add(nextHandler);
                return nextHandler;
            }
        }
        if (previousFactory != null) {
            URLStreamHandler previousHandler = previousFactory.createURLStreamHandler(protocol);
            return previousHandler;
        }
        return null;
    }
}
