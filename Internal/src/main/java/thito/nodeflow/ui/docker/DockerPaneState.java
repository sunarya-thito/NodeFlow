package thito.nodeflow.ui.docker;

import java.io.Serial;
import java.io.Serializable;

public class DockerPaneState implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public TabPaneState center, left, right, bottom;

    public double[] dividerPositions;

    public static class TabPaneState implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;
        public int selectedIndex;
        public TabState[] tabs;
    }
    public static class TabState implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;
        public DockNodeState componentState;
    }
}
