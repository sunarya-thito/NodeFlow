package thito.nodeflow.internal;

import java.io.*;
import java.nio.charset.*;
import java.text.*;
import java.util.*;
import java.util.logging.*;

public class LoggerInjector extends ConsoleHandler {
    public static void init() throws IOException {
        Logger logger = Logger.getLogger("");
        for (Handler handler : logger.getHandlers()) logger.removeHandler(handler);
        String format = "Logs/logs " + new SimpleDateFormat("dd-MM-yyyy").format(new Date()) + " %s.log";
        int uniqueIndex = 0;
        while (new File(String.format(format, uniqueIndex)).exists()) uniqueIndex++;
        FileHandler handler = new FileHandler(String.format(format, uniqueIndex), false);
        handler.setFormatter(new NodeFlowLogFormatter());
        logger.addHandler(handler);
        logger.addHandler(new LoggerInjector());
        System.setOut(new PrintStream(new LoggerPrintStream(NodeFlow.getLogger(), Level.INFO), true));
        System.setErr(new PrintStream(new LoggerPrintStream(NodeFlow.getLogger(), Level.SEVERE), true));
    }

    public LoggerInjector() {
        setFormatter(new NodeFlowLogFormatter());
        setOutputStream(System.out);
    }

    public static class LoggerPrintStream extends OutputStream {
        private final Logger logger;
        private final Level logLevel;
        private final ByteArrayOutputStream buffer = new ByteArrayOutputStream(1024 * 8);

        public LoggerPrintStream(Logger logger, Level logLevel) {
            this.logger = logger;
            this.logLevel = logLevel;
        }

        @Override
        public void write(int b) throws IOException {
            buffer.write(b);
        }

        @Override
        public void flush() throws IOException {
            if (buffer.size() <= 0) return;
            logger.log(logLevel, buffer.toString(StandardCharsets.UTF_8));
            buffer.reset();
        }
    }
}
