package thito.nodeflow.internal.ui;

public enum Corner {
    TOP_LEFT(HitTestAction.TOP_LEFT), TOP(HitTestAction.TOP), TOP_RIGHT(HitTestAction.TOP_RIGHT),
    CENTER_LEFT(HitTestAction.LEFT), CENTER(HitTestAction.CLIENT), CENTER_RIGHT(HitTestAction.RIGHT),
    BOTTOM_LEFT(HitTestAction.BOTTOM_LEFT), BOTTOM(HitTestAction.BOTTOM), BOTTOM_RIGHT(HitTestAction.BOTTOM_RIGHT),
    OUTSIDE(HitTestAction.NOWHERE);
    HitTestAction hitTestAction;

    Corner(HitTestAction hitTestAction) {
        this.hitTestAction = hitTestAction;
    }

    public HitTestAction getHitTestAction() {
        return hitTestAction;
    }
}
