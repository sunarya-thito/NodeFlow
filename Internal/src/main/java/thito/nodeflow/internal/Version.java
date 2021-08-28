package thito.nodeflow.internal;

import javafx.collections.*;

import java.io.*;

public class Version {

    private static Version currentVersion;
    private static ObservableList<Version> versions = FXCollections.observableArrayList();

    public static void read(Reader reader) throws IOException {
        try (BufferedReader bufferedReader = new BufferedReader(reader)) {
            String version = null;
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.startsWith("[") && line.endsWith("]")) {
                    if (version != null) {
                        versions.add(new Version(version, builder.toString()));
                    }
                    version = line.substring(1, line.length() - 1);
                    builder = new StringBuilder();
                    continue;
                }
                if (!builder.isEmpty()) {
                    builder.append('\n');
                }
                builder.append(line);
            }
            if (version != null) {
                versions.add(new Version(version, builder.toString()));
            }
        }
        setCurrentVersion(versions.get(0));
    }

    public static Version getCurrentVersion() {
        return currentVersion;
    }

    public static void setCurrentVersion(Version currentVersion) {
        Version.currentVersion = currentVersion;
    }

    public static ObservableList<Version> getVersions() {
        return versions;
    }

    private String version;
    private String changeLog;

    public Version(String version, String changeLog) {
        this.version = version;
        this.changeLog = changeLog;
    }

    public String getVersion() {
        return version;
    }

    void setVersion(String version) {
        this.version = version;
    }

    public String getChangeLog() {
        return changeLog;
    }

    public int getIndex() {
        return versions.indexOf(this);
    }

    @Override
    public String toString() {
        return "Version{" +
                "version='" + version + '\'' +
                '}';
    }
}
