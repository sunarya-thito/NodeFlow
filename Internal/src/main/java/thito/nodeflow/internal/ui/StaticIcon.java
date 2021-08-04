package thito.nodeflow.internal.ui;

import thito.nodeflow.api.resource.*;
import thito.nodeflow.api.ui.*;

import java.io.*;

public class StaticIcon extends ImageImpl implements RandomAccessIcon, RandomAccessImage {

    public StaticIcon(InputStream inputStream) {
        super(inputStream);
    }

    public StaticIcon(ResourceFile file) {
        super(file);
    }

    @Override
    public void applyTheme(Theme theme) {
    }

    @Override
    public void dispose() {
        peerProperty.set(null);
    }

}
