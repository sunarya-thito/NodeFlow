package thito.nodeflow.api;

import thito.nodeflow.api.task.FutureSupplier;

import java.util.List;

public interface Updater {
    FutureSupplier<Version> fetchLatestUpdate();
    FutureSupplier<List<Update>> fetchChangeLogs();
}
