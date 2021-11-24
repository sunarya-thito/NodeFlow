package thito.nodeflow.settings.canvas;

public @interface FileItem {
    boolean mustExist() default true;
    FileMode mode();
    String[] filters() default {"Any *"};
}
