package thito.nodeflow.api.resource;

import java.io.InputStream;
import java.io.Reader;

public interface ResourceFile extends Resource {
    String getFileName(); // with extension!

    String getExtension();

    Reader openReader();

    InputStream openInput();

    byte[] readAll();
}
