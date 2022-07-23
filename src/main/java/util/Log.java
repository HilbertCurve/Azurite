package util;

import java.io.File;
import java.io.IOException;

/**
 * Logging utility to print colored and labeled errors with class references.
 */
public class Log {

    public static final int FATAL_ONLY = 0; //fatal
    public static final int WARNINGS = 1; //fatal, warn
    public static final int NO_DEBUG = 2; //fatal, warn, info
    public static final int ALL = 3; //fatal, warn, info, debug

    public static final String CL_RED = "\u001b[31m";
    public static final String CL_GREEN = "\u001b[32m";
    public static final String CL_ORANGE = "\u001b[33m";
    public static final String CL_BLUE = "\u001b[34m";
    public static final String CL_PURPLE = "\u001b[35m";
    public static final String CL_CYAN = "\u001b[36m";
    public static final String CL_GRAY = "\u001b[37m";

    private static int logLevel = FATAL_ONLY;

    private static LoggingThread loggingThread;

    private static StackTraceElement source() {
        return Thread.currentThread().getStackTrace()[3];
    }

    public synchronized static void startLogging(File path, int logLevel) throws IOException {
        if (loggingThread != null) return;
        loggingThread = new LoggingThread(path, logLevel);
        if (!loggingThread.init()) {
            System.out.println("There is some fatal issue with the path \"" + path.toString() + "\"");
            return;
        }
        loggingThread.start();
    }

    public static void setLogLevel(int logLevel) {
        Log.logLevel = Math.abs(logLevel) % 4;
    }

    public static void debug(String log) {
        StackTraceElement source = source();
        String line = "[DEBUG] {" + source.getClassName() + "} " + log;
        println(ALL, CL_CYAN + line);
    }

    public static void debug(String Log, boolean printSource) {
        StackTraceElement source = source();
        String line = "[DEBUG]" + (printSource ? " {" + source.getClassName() + "} " : " ") + Log;
        println(ALL, CL_CYAN + line);
    }

    public static void info(String info) {
        StackTraceElement source = source();
        String line = "[INFO] {" + source.getClassName() + "} " + info;
        println(NO_DEBUG, CL_GREEN + line);
    }

    public static void info(String info, boolean printSource) {
        StackTraceElement source = source();
        String line = "[INFO]" + (printSource ? " {" + source.getClassName() + "} " : " ") + info;
        println(NO_DEBUG, CL_GREEN + line);
    }

    public static void warn(String warn) {
        StackTraceElement source = source();
        String line = "[WARN] {" + source.getClassName() + "} " + warn;
        println(WARNINGS, CL_ORANGE + line);
    }

    public static void warn(String warn, boolean printSource) {
        StackTraceElement source = source();
        String line = "[WARN]" + (printSource ? " {" + source.getClassName() + "} " : " ") + warn;
        println(WARNINGS, CL_ORANGE + line);
    }

    public static void fatal(String error) {
        StackTraceElement source = source();
        String line = "[FATAL] {" + source + "} " + error;
        println(FATAL_ONLY, CL_RED + line);
    }

    public static void fatal(String error, boolean printSource) {
        StackTraceElement source = source();
        String line = "[FATAL]" + (printSource ? " {" + source + "} " : " ") + error;
        println(FATAL_ONLY, CL_RED + line);
    }

    public static void crash(Exception e) {
        String line = "[CRASH] " + e.getMessage();
        System.out.println(CL_RED + line);
        e.printStackTrace();
    }

    private static void println(int level, String line) {
        if (logLevel >= level)
            System.out.println(line);
        loggingThread.log(line, level);
    }

}