package thito.nodeflow.api.ui;

public enum Pos {
    TOP_LEFT(H.LEFT, V.TOP),
    TOP(H.CENTER, V.TOP),
    TOP_RIGHT(H.RIGHT, V.TOP),

    LEFT(H.LEFT, V.CENTER),
    CENTER(H.CENTER, V.CENTER),
    RIGHT(H.RIGHT, V.CENTER),

    BOTTOM_LEFT(H.LEFT, V.BOTTOM),
    BOTTOM(H.CENTER, V.BOTTOM),
    BOTTOM_RIGHT(H.RIGHT, V.BOTTOM),

    // Special, text alignment
    JUSTIFY(H.LEFT, V.TOP);
    private final H h;
    private final V v;

    Pos(H h, V v) {
        this.h = h;
        this.v = v;
    }

    public H getHorizontal() {
        return h;
    }

    public V getVertical() {
        return v;
    }

    public enum H {
        LEFT,
        CENTER,
        RIGHT
    }

    public enum V {
        TOP,
        CENTER,
        BOTTOM
    }
}
