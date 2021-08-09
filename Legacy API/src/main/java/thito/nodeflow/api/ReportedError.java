package thito.nodeflow.api;

public class ReportedError extends Error {

    public ReportedError(String message, Throwable cause) {
        super(message, cause);
    }

    public ReportedError(Throwable cause) {
        super(cause);
    }
}
