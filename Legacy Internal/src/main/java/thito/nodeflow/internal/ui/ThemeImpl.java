package thito.nodeflow.internal.ui;

import thito.nodeflow.api.ui.*;

import java.util.*;

public class ThemeImpl implements Theme {

    private final String name;

    public ThemeImpl(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getIconDirectoryPath() {
        return getName();
    }

    public String[] getCSSPaths(Window window) {
        if (window == null) {
            return new String[] {"rsrc:themes/" + getName() + "/Global.css"};
        }
        return new String[] {
                "rsrc:themes/" + getName()+ "/Global.css",
                "rsrc:themes/" + getName() + "/" + window.getName() + ".css"
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ThemeImpl theme = (ThemeImpl) o;
        return name.equalsIgnoreCase(theme.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return getName();
    }
}
