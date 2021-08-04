package thito.nodeflow.internal;

import java.util.*;

public class ExceptionParser {

    public static List<ParsedException> parseExceptions(String string) {
        List<ParsedException> list = new ArrayList<>();
        while (string != null) {
            int index = string.indexOf("!!NodeException: ");
            if (index >= 0) {
                string = string.substring(index + "!!NodeException: ".length());
                try {
                    index = string.indexOf(" (");
                    UUID id = UUID.fromString(string.substring(0, index));
                    string = string.substring(index + " (".length());
                    index = string.indexOf(')');
                    String name = string.substring(0, index);
                    string = string.substring(index + ")".length());
                    list.add(new ParsedException(name, id));
                } catch (Throwable t) {
                }
            } else break;
        }
        return list;
    }
    public static class ParsedException {
        private String fileName;
        private UUID uuid;

        public ParsedException(String fileName, UUID uuid) {
            this.fileName = fileName;
            this.uuid = uuid;
        }

        public String getFileName() {
            return fileName;
        }

        public UUID getID() {
            return uuid;
        }

        @Override
        public String toString() {
            return "ParsedException{" +
                    "fileName='" + fileName + '\'' +
                    ", uuid=" + uuid +
                    '}';
        }
    }
}
