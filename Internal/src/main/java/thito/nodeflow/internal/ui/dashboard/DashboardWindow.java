package thito.nodeflow.internal.ui.dashboard;

import thito.nodeflow.library.ui.*;

public class DashboardWindow extends Window {

    private static DashboardWindow window;

    public static DashboardWindow getWindow() {
        return window;
    }

    public DashboardWindow() {
        window = this;
    }

    @Override
    protected Skin createSkin() {
        return new DashboardSkin();
    }

}
