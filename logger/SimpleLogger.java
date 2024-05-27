package logger;

import logger.custom.SimpleLevel;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

public class SimpleLogger {

    /**
     * The Default Path where logs are generated. The location is folder log inside project directory.
     * If it doesn't exist, it will be created automatically.
     */
    public static final String DEFAULT_LOG_PATH = System.getProperty("user.dir") + "/log";

    // Depth of stack trace
    private static final int DEPTH_LOG_STACK_TRACE = 5;
    // Usage Class Name
    private String className;
    // Java Logger
    private Logger logger;
    // Custom Formatter
    private static class CustomFormatter extends Formatter {
        @Override
        public String format(LogRecord record) {
            return "[" + new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date(record.getMillis())) + "][" + record.getLevel() + " - " + record.getMessage() + "\n";
        }
    }


    // Constructor
    public SimpleLogger() { /* ... */ }

    /**
     * Initialize the SimpleLogger without File Handler
     * 
     * @param className Name of Class that use the SimpleLogger
     * @return SimpleLogger instance
     */
    public SimpleLogger initLogger(String className) {
        return initLogger(className, null);
    }

    /**
     * Initialize the SimpleLogger class
     * 
     * @param className Name of Class that use the SimpleLogger
     * @param logPath The path of Log folder
     * @return SimpleLogger instance
     */
    public SimpleLogger initLogger(String className, String logPath) {
        this.className = className;

        try {
            // Java Logger initialization
            this.logger = Logger.getLogger(this.className);
            // Remove all default formatters
            LogManager.getLogManager().reset();
            // Custom Formatter
            CustomFormatter customFormatter = new CustomFormatter();

            // Create the Log folder if logPath is not null
            if (logPath != null) {
                // If is empty, set default value
                if (logPath.trim().isEmpty()) logPath = DEFAULT_LOG_PATH;
                // Construct the log name
                String fileLogPath = logPath + "/Run_" + new SimpleDateFormat("dd-MM-yyyy_HHmmss").format(new Date(System.currentTimeMillis())) + ".log";

                Path path = Paths.get(logPath);
                if (!Files.exists(path))
                    Files.createDirectory(path);

                // Create and add File Handler
                FileHandler fHandler = new FileHandler(fileLogPath, true);
                fHandler.setFormatter(customFormatter);
                logger.addHandler(fHandler);
            }

            // Create and add Console Handler
            ConsoleHandler cHandler = new ConsoleHandler();
            cHandler.setFormatter(customFormatter);
            logger.addHandler(cHandler);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return this;
    }

    /**
     * Write a message with severity level INFO
     * 
     * @param msg The message that will be written in the log file
     */
    public void info(String msg) {
        writeLog(Level.INFO, msg);
    }

    /**
     * Write a message with severity level INFO
     * 
     * @param msg The message that will be written to log
     * @param className The name of class that will be appeared in the line
     */
    public void info(String msg, String className) {
        writeLog(Level.INFO, className, msg);
    }

    /**
     * Write a message with severity level WARNING
     * 
     * @param msg The message that will be written in the log file
     */
    public void warning(String msg) {
        writeLog(Level.WARNING, msg);
    }

    /**
     * Write a message with severity level WARNING
     * 
     * @param msg The message that will be written to log
     * @param className The name of class that will be appeared in the line
     */
    public void warning(String msg, String className) {
        writeLog(Level.WARNING, msg);
    }

    /**
     * Write a message with severity level ERROR
     * 
     * @param msg The message that will be written in the log file
     */
    public void error(String msg) {
        writeLog(SimpleLevel.ERROR, msg);
    }

    /**
     * Write a message with severity level ERROR
     * 
     * @param msg The message that will be written to log
     * @param className The name of class that will be appeared in the line
     */
    public void error(String msg, String className) {
        writeLog(SimpleLevel.ERROR, msg);
    }

    /**
     * Log a message
     * 
     * @param level Level of severity
     * @param msg The message that will be written in the log file
     */
    public void writeLog(Level level, String msg) {
        writeLog(level, msg, this.className);
    }

    /**
     * Log a message
     * 
     * @param level Level of severity
     * @param msg The message that will be written in the log file
     * @param className The name of class that will be appeared in the line
     */
    public void writeLog(Level level, String msg, String className) {
        logger.log(level, className + ":" + this.getLineNumber() + "] " + msg);
    }

    /**
     * Get the current line number.
     * 
     * @return int - Current line number.
     */
    private int getLineNumber() {
        return Thread.currentThread()
                .getStackTrace()[DEPTH_LOG_STACK_TRACE]
                .getLineNumber();
    }
}
