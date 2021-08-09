package thito.nodeflow.api.resource;

import java.io.OutputStream;
import java.io.Writer;

public interface WritableResourceFile extends ResourceFile {
    Writer openWriter();

    OutputStream openOutput();

    void writeAll(byte[] bytes);
}
