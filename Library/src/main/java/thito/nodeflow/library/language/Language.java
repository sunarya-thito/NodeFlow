package thito.nodeflow.library.language;

import javafx.beans.*;
import javafx.beans.property.*;
import javafx.beans.value.*;
import thito.nodeflow.library.config.*;

import java.io.*;
import java.util.*;
import java.util.logging.*;

public class Language {
    private static final Logger logger = Logger.getLogger(Language.class.getName());

    private static ObjectProperty<Language> language = new SimpleObjectProperty<>();

    public static ObjectProperty<Language> languageProperty() {
        return language;
    }

    public static Language getLanguage() {
        return language.get();
    }

    public static void setLanguage(Language language) {
        Language.language.set(language);
    }

    private String code;
    private Map<String, I18n> items = new HashMap<>();

    public Language(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Language)) return false;
        Language language = (Language) o;
        return code.equals(language.code);
    }

    @Override
    public String toString() {
        return getItem("name").getValueSafe();
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    public void loadLanguage(Reader reader) {
        MapSection section = Section.parseToMap(reader);
        loadLanguage(section, null);
    }

    public void loadLanguage(MapSection section) {
        loadLanguage(section, null);
    }

    public void loadLanguage(MapSection section, String parent) {
        section.forEach((key, value) -> {
            String fullKey = parent == null ? key : parent + "." + key;
            if (value instanceof Map) {
                loadLanguage(new MapSection((Map<String, ?>) value), fullKey);
                return;
            }
            logger.log(Level.INFO, "Loaded language: "+fullKey);
            items.put(fullKey, new I18n(String.valueOf(value)));
        });
    }

    public I18n getItem(String key) {
        I18n item = items.get(key);
        if (item != null) return item;
        I18n newItem = new I18n();
        items.put(key, newItem);
        return newItem;
    }

    public ObservableValue<String> replace(ObservableValue<String> property) {
        return new SimpleStringProperty() {
            Set<ObservableValue<String>> observing = new HashSet<>();
            InvalidationListener listener = o -> update();

            {
                property.addListener(listener);
                update();
            }

            void update() {
                String s = property.getValue();
                if (s == null) {
                    set(null);
                    return;
                }
                StringBuilder builder = new StringBuilder(s.length());
                StringBuilder path = new StringBuilder();
                Set<ObservableValue<String>> currentObserver = new HashSet<>();
                for (int i = 0; i < s.length(); i++) {
                    char c = s.charAt(i);
                    if (c == '$') {
                        if (path.length() == 0) {
                            path.append(c);
                            continue;
                        }
                    } else if (c == '{') {
                        if (path.length() == 1) {
                            path.append(c);
                            continue;
                        }
                    } else if (c == '}') {
                        if (path.length() >= 2) {
                            I18n item = getItem(path.substring(2));
                            currentObserver.add(item);
                            builder.append(item.get());
                            path.delete(0, path.length());
                            continue;
                        }
                    } else {
                        if (path.length() >= 2) {
                            path.append(c);
                            continue;
                        }
                    }
                    builder.append(c);
                }
                observing.removeIf(observer -> {
                    if (!currentObserver.contains(observer)) {
                        observer.removeListener(listener);
                        return true;
                    }
                    return false;
                });
                currentObserver.removeAll(observing);
                for (ObservableValue<String> observer : currentObserver) {
                    observing.add(observer);
                    observer.addListener(listener);
                }
                set(builder.toString());
            }
        };
    }

}
