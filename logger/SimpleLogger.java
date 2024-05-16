package programs.logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

import static programs.GlobalVariables.LOG_PATH;

public class SimpleLogger {

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
     * Initialize the SimpleLogger
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
                if (logPath.trim().isEmpty()) logPath = LOG_PATH;
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

    public void info(String msg) {
        writeLog(Level.INFO, msg);
    }

    public void warning(String msg) {
        writeLog(Level.WARNING, msg);
    }

    public void severe(String msg) {
        writeLog(Level.SEVERE, msg);
    }

    public void writeLog(Level level, String msg) {
        writeLog(level, msg, this.className);
    }

    public void writeLog(Level level, String msg, String className) {
        logger.log(level, className + ":" + this.getLineNumber() + "] " + msg);
    }

    /**
     * Get the current line number.
     * @return int - Current line number.
     */
    private int getLineNumber() {
        return Thread.currentThread()
                .getStackTrace()[3]
                .getLineNumber();
    }

}

