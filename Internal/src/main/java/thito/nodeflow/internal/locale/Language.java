package thito.nodeflow.internal.locale;

import thito.nodeflow.api.*;
import thito.nodeflow.api.resource.*;

import java.util.*;

public class Language implements Comparable<Language> {
    private Locale locale;

    public Language(String code) {
        locale = new Locale(code);
    }

    public Language(Locale locale) {
        this.locale = locale;
    }

    @Override
    public int compareTo(Language o) {
        return locale.getDisplayName().compareTo(o.locale.getDisplayName());
    }

    public Locale getLocale() {
        return locale;
    }

    public boolean isAvailable() {
        return getFile() instanceof ResourceFile;
    }

    public Resource getFile() {
        return NodeFlow.getApplication().getResourceManager().getResource("locales/"+locale.getLanguage()+".properties");
    }

    public void load() {
        Resource file = getFile();
        if (file instanceof ResourceFile && !(file instanceof UnknownResource)) {
            NodeFlow.getApplication().getLocaleManager().loadLocale((ResourceFile) file);
        }
    }

    public String toString() {
        return locale.getDisplayLanguage() + " ("+locale.getDisplayLanguage(locale)+")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Language language = (Language) o;
        return locale.equals(language.locale);
    }

    @Override
    public int hashCode() {
        return Objects.hash(locale);
    }
}
