package thito.nodeflow.internal.ui;

import thito.nodeflow.api.resource.*;
import thito.nodeflow.api.ui.*;

public class RandomAccessThemeImpl extends ThemeImpl implements RandomAccessTheme {
    private final RandomAccessResourceFile randomAccessResourceFile;
    public RandomAccessThemeImpl(String name, RandomAccessResourceFile randomAccessResourceFile) {
        super(name);
        this.randomAccessResourceFile = randomAccessResourceFile;
    }

    @Override
    public String[] getCSSPaths(Window name) {
        return new String[] {"rar:"+randomAccessResourceFile.getName()};
    }

    @Override
    public void dispose() {
        randomAccessResourceFile.dispose();
    }
}
