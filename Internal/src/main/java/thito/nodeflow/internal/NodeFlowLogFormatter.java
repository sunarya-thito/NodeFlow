package thito.nodeflow.internal;

import java.io.*;
import java.time.*;
import java.util.*;
import java.util.logging.*;
import java.util.logging.Formatter;

public class NodeFlowLogFormatter extends Formatter {
    private static Map<Level, String> nameMapping = new HashMap<>();
    static {
        nameMapping.put(Level.SEVERE, "ERR");
        nameMapping.put(Level.INFO, "INF");
        nameMapping.put(Level.FINE, "FIN");
        nameMapping.put(Level.CONFIG, "CON");
        nameMapping.put(Level.ALL, "ALL");
        nameMapping.put(Level.FINER, "FI2");
        nameMapping.put(Level.FINEST, "FI3");
        nameMapping.put(Level.OFF, "OFF");
        nameMapping.put(Level.WARNING, "WAR");
    }
    @Override
    public String format(LogRecord record) {
        ZonedDateTime zdt = ZonedDateTime.ofInstant(
                record.getInstant(), ZoneId.systemDefault());
        String source = Thread.currentThread().getName();
        String message = formatMessage(record);
        String throwable = "";
        Throwable thrown = record.getThrown();
        if (thrown != null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            thrown.printStackTrace(pw);
            pw.close();
            throwable = sw.toString();
        }
        StringBuilder builder = new StringBuilder(
                '[' +
                        leadingZeros(zdt.getDayOfMonth(), 2) +
                        '-' +
                        leadingZeros(zdt.getMonth().getValue(), 2) +
                        '-' +
                        zdt.getYear() +
                        ' ' +
                        leadingZeros(zdt.getSecond(), 2) +
                        ':' +
                        leadingZeros(zdt.getMinute(), 2) +
                        ':' +
                        leadingZeros(zdt.getHour(), 2) +
                        "] [" +
                        nameMapping.get(record.getLevel()) +
                        "] [" +
                        source +
                        "] " +
                        message.trim() +
                        '\n'
        );
        if (!throwable.isEmpty()) {
            for (String str : throwable.split("\n")) {
                String format = '[' +
                        leadingZeros(zdt.getDayOfMonth(), 2) +
                        '-' +
                        leadingZeros(zdt.getMonth().getValue(), 2) +
                        '-' +
                        zdt.getYear() +
                        ' ' +
                        leadingZeros(zdt.getSecond(), 2) +
                        ':' +
                        leadingZeros(zdt.getMinute(), 2) +
                        ':' +
                        leadingZeros(zdt.getHour(), 2) +
                        "] [" +
                        nameMapping.get(record.getLevel()) +
                        "] [" +
                        source +
                        "] " +
                        str +
                        '\n';
                builder.append(format);
            }
        }
        return builder.toString();
    }

    static String leadingZeros(int value, int amount) {
        StringBuilder string = new StringBuilder(Integer.toString(value));
        while (string.length() < amount) string.insert(0, "0");
        return string.toString();
    }
}
