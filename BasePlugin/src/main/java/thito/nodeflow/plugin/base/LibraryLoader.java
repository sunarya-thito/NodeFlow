package thito.nodeflow.plugin.base;

import thito.nodeflow.task.batch.Batch;

import java.io.File;
import java.util.function.Consumer;

public interface LibraryLoader {
    boolean accept(File file);
    String getHashCode(File file);
    Batch.Task loadLibrary(File file, Consumer<Library> loadedLibraryConsumer);
}
