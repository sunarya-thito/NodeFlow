package thito.nodeflow.internal.application;

import thito.nodeflow.internal.language.*;
import thito.nodeflow.internal.ui.*;

import java.net.*;
import java.util.*;

public abstract class ApplicationResources {

    private static ApplicationResources instance;

    public static ApplicationResources getInstance() {
        if (instance == null) throw new IllegalStateException("not initialized");
        return instance;
    }

    private Map<String, URLStreamHandler> protocolHandlerMap = new HashMap<>();

    public ApplicationResources() {
        if (instance != null) throw new IllegalStateException("multiple ApplicationResources detected");
        instance = this;
        URL.setURLStreamHandlerFactory(this::getProtocolHandler);
    }

    public void registerProtocol(String protocol, URLStreamHandler handler) {
        protocolHandlerMap.put(protocol, handler);
    }

    private URLStreamHandler getProtocolHandler(String protocol) {
        return protocolHandlerMap.get(protocol);
    }

    public abstract Collection<? extends Language> getAvailableLanguages();
    public abstract Collection<? extends Theme> getAvailableThemes();
    public abstract Language getDefaultLanguage();

}
