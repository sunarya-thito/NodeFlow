package thito.nodeflow.library.ui;

import javafx.beans.property.*;
import thito.nodeflow.api.task.*;
import thito.nodeflow.internal.*;

import java.util.*;

public class Ticker {
    private static WeakHashMap<Tickable, Boolean> tickables = new WeakHashMap<>();
    public static final LongProperty TIME = new SimpleLongProperty(0L);
    static {
        Task.runOnForegroundRepeatedly("ticker", () -> {
            TIME.set(System.currentTimeMillis());
            synchronized (tickables) {
                tickables.size(); // garbage collect things up
                for (Tickable tickable : tickables.keySet()) {
                    try {
                        tickable.tick();
                    } catch (Throwable t) {
                        Toolkit.error("Ticking error", t);
                        t.printStackTrace();
                    }
                }
            }
        }, Duration.millis(16), Duration.millis(16));
    }
    public static boolean isRegistered(Tickable tickable) {
        synchronized (tickables) {
            return tickables.containsKey(tickable);
        }
    }
    public static void register(Tickable tickable) {
        synchronized (tickables) {
            tickables.put(tickable, Boolean.FALSE);
        }
    }
    public static void unregister(Tickable tickable) {
        synchronized (tickables) {
            tickables.remove(tickable);
        }
    }
}
