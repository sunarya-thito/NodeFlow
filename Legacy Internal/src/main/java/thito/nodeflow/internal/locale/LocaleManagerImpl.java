package thito.nodeflow.internal.locale;

import com.dlsc.formsfx.model.util.*;
import thito.nodeflow.api.*;
import thito.nodeflow.api.locale.*;
import thito.nodeflow.api.resource.*;

import java.io.*;
import java.nio.charset.*;
import java.util.*;
import java.util.logging.*;

public class LocaleManagerImpl extends TranslationService implements LocaleManager {
    private final Map<String, I18nItem> localeItemMap = new HashMap<>();
    @Override
    public I18nItem getItem(String name) {
        return localeItemMap.computeIfAbsent(name, I18nItemImpl::new);
    }

    @Override
    public void loadLocale(ResourceFile file) {
        notifyListeners();
        try (Reader reader = new InputStreamReader(file.openInput(), StandardCharsets.UTF_8)) {
            Properties properties = new Properties();
            properties.load(reader);
            properties.forEach((key, value) -> {
                getItem(key.toString()).setRawString(value.toString());
                NodeFlow.getApplication().getLogger().log(Level.INFO, "Loaded lang key: "+key+" = "+value);
            });
            NodeFlow.getApplication().getLogger().log(Level.INFO, "Loaded locale: "+getItem("name")+" ["+getItem("code")+"]");
        } catch (Throwable t) {
            throw new ReportedError(t);
        }
    }

    @Override
    public String translate(String s) {
        return getItem(s).getString();
    }

    @Override
    public ResourceBundle asBundle() {
        return resourceBundleLocaleManager;
    }

    private ResourceBundleLocaleManagerImpl resourceBundleLocaleManager = new ResourceBundleLocaleManagerImpl();
    class ResourceBundleLocaleManagerImpl extends ResourceBundle {
        @Override
        protected Object handleGetObject(String key) {
            return getItem(key).getString();
        }

        @Override
        public Enumeration<String> getKeys() {
            return Collections.enumeration(localeItemMap.keySet());
        }
    }
}
