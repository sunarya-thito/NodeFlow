package thito.nodeflow.internal;

import thito.nodeflow.api.*;

import java.util.*;

public class UpdateImpl implements Update {
    private Version version;
    private String changeLogs;

    public UpdateImpl(Version version, String changeLogs) {
        this.version = version;
        this.changeLogs = changeLogs;
    }

    @Override
    public Version getVersion() {
        return version;
    }

    @Override
    public String getChangeLogs() {
        return changeLogs;
    }

    @Override
    public String toString() {
        return "UpdateImpl{" +
                "version=" + version +
                ", changeLogs='" + changeLogs + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UpdateImpl update = (UpdateImpl) o;
        return version.equals(update.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(version);
    }
}
