package thito.nodeflow.internal;

import thito.nodeflow.api.*;
import thito.nodeflow.api.resource.*;
import thito.nodeflow.api.task.*;

import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.util.*;

public class UpdaterImpl implements Updater {
    private static final String fetchLatestVersionURL = "";
    private static final String fetchChangelogsURL = "";

    public static List<Update> parse(String file) {
        String[] lines = file.split("\n");
        List<Update> updates = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        for (int i = lines.length - 1; i >= 0; i--) {
            String line = lines[i].trim();
            if (line.startsWith("[") && line.endsWith("]")) {
                line = line.substring(1, line.length() - 1);
                updates.add(0, new UpdateImpl(VersionImpl.parse(line), builder.toString()));
                builder = new StringBuilder();
            } else {
                builder.insert(0, line);
                builder.insert(0, '\n');
            }
        }
        return updates;
    }

    @Override
    public FutureSupplier<Version> fetchLatestUpdate() {
        return FutureSupplier.executeOnBackground(() -> {
            try {
                URL url = new URL(fetchLatestVersionURL);
                URLConnection connection = url.openConnection();
                InputStream inputStream = connection.getInputStream();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[32];
                int len;
                while ((len = inputStream.read(buffer, 0, buffer.length)) != -1) {
                    outputStream.write(buffer, 0, len);
                }
                inputStream.close();
                return VersionImpl.parse(new String(outputStream.toByteArray()));
            } catch (Throwable t) {
                return null;
            }
        });
    }

    @Override
    public FutureSupplier<List<Update>> fetchChangeLogs() {
        return FutureSupplier.executeOnBackground(() -> {
            try {
                URL url = new URL(fetchChangelogsURL);
                URLConnection connection = url.openConnection();
                InputStream inputStream = connection.getInputStream();
                return fetchUpdate(inputStream);
                // inputStream is already closed at this point
            } catch (Throwable t) {
                return fetchUpdate(((ResourceFile) NodeFlow.getApplication().getResourceManager().getResource("Changelogs.txt")).openInput());
            }
        });
    }

    private List<Update> fetchUpdate(InputStream inputStream) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            int len; byte[] buffer = new byte[1024 * 8];
            while ((len = inputStream.read(buffer, 0, buffer.length)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            inputStream.close();
            return parse(new String(outputStream.toByteArray(), StandardCharsets.UTF_8));
        } catch (Throwable t) {
        }
        return new ArrayList<>();
    }
}
