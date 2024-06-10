package it.simple.logger;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import java.util.logging.*;

public class SimpleLogger {

    /**
     * This constant is used to tell SimpleLogger not to create a log file
     */
    public static final String NO_FILE = "NoFileHandler";

    // By default, log will be created inside project folder.
    private static final String DEFAULT_LOG_PATH = System.getProperty("user.dir") + "/log";
    /**
     * Depth of stack trace. The depth depends on how many function are called.
     * <li>
     *     Example:
     *     <ul>0 = Line of return array of StackTrace in {@code getStackTrace()}</ul>
     *     <ul>1 = Line of where {@code getStackTrace()} is called</ul>
     *     <ul>2 = Line of where the function that contains the call of {@code getStackTrace()} is called</ul>
     *     <ul>Etc..</ul>
     * </li>
     */
    private static final int DEPTH_LOG_STACK_TRACE = 4;
    // Java Logger
    private Logger logger;
    // Name of class printed to log
    private String className;
    // Location of log file
    private String path;

    // ------------------------ Custom Classes ------------------------
    // Custom Formatter
    private static class CustomFormatter extends Formatter {
        @Override
        public String format(LogRecord record) {
            return "[" + new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date(record.getMillis())) + "][" + record.getLevel() + " - " + record.getMessage() + "\n";
        }
    }

    // Custom Level
    private static class SimpleLevel extends Level {
        protected SimpleLevel(String name, int value) {
            super(name, value);
        }
    }

    /**
     * ERROR is a message level that represent that something gone wrong.
     * Is similar to SEVERE Level but the value is more high. It should be used to display possible errors.
     */
    public static final SimpleLevel ERROR = new SimpleLevel("ERROR", 1100);

    // Constructor
    protected SimpleLogger() {}

    /**
     * <p style="text-align: justify">
     * Create a new instance of {@link SimpleLogger}.<br/>
     * When the {@code path} parameter is set with the value of the constant {@link #NO_FILE}, the log file is not
     * created. If instead is set with an empty string or null, the file will be created inside the project folder.
     * The {@link SimpleLogger} should be declared in main class and passed by parameter in other classes.
     * It provided of function with {@code classname} as parameter to log message from other classes.
     * </p>
     *
     * @param className The name of class
     * @param path The location of folder where put the file.
     * @return A new instance of SimpleLogger
     */
    public static SimpleLogger create(String className, String path) {
        // Create an instance of SimpleLogger
        SimpleLogger instance = new SimpleLogger();
        try {
            instance.build(className, path);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return instance;
    }

    /**
     * <p style="text-align: justify;">
     * Build the {@link SimpleLogger}.
     * </p>
     * @param className The name of class
     * @param path The location of folder where put the file.
     * @throws Exception If something goes wrong.
     */
    private void build(String className, String path) throws Exception {
        this.className = className;
        // Java Logger initialization
        this.logger = Logger.getLogger(this.className);
        // Remove all default formatters
        LogManager.getLogManager().reset();
        // Create the new custom formatter
        CustomFormatter customFormatter = new CustomFormatter();

        // Create and add Console Handler
        ConsoleHandler cHandler = new ConsoleHandler();
        cHandler.setFormatter(customFormatter);
        logger.addHandler(cHandler);

        // Avoid NullPointerException
        path = Optional.ofNullable(path).orElse("");
        // Check if user want to create a log file
        if (!path.equals(NO_FILE)) {
            // If is empty, set default location
            if (path.trim().isEmpty()) path = DEFAULT_LOG_PATH;
            this.path = path;
            // Construct the log name
            String fileLogPath = path + "/Run_" + new SimpleDateFormat("dd-MM-yyyy_HHmmss").format(new Date(System.currentTimeMillis())) + ".log";

            // Create the folder if not exists
            Path log_path = Paths.get(path);
            if (!Files.exists(log_path))
                Files.createDirectory(log_path);

            // Create and add File Handler
            FileHandler fHandler = new FileHandler(fileLogPath, true);
            fHandler.setFormatter(customFormatter);
            logger.addHandler(fHandler);
        }
    }


    /**
     * Log a message<br/>
     * This function is the basis of all others write functions.
     *
     * @param level The level of message
     * @param msg The message that will be written
     * @param className The name of class that will be displayed on log message
     */
    private void write(Level level, String msg, String className) {
        logger.log(level, className + ":" + this.getLineNumber() + "] " + msg);
    }

    /**
     * Log an INFO message.
     *
     * @param msg The message that will be written
     */
    public void info(String msg) {
        this.write(Level.INFO, msg, this.className);
    }

    /**
     * Log an INFO message. It can be used to other classes.
     *
     * @param msg The message that will be written
     * @param className The name of class that will be displayed on log message
     */
    public void info(String msg, String className) {
        this.write(Level.INFO, msg, className);
    }

    /**
     * Log an WARNING message.
     *
     * @param msg The message that will be written
     */
    public void warning(String msg) {
        this.write(Level.WARNING, msg, this.className);
    }

    /**
     * Log an WARNING message. It can be used to other classes.
     *
     * @param msg The message that will be written
     * @param className The name of class that will be displayed on log message
     */
    public void warning(String msg, String className) {
        this.write(Level.WARNING, msg, className);
    }

    /**
     * Log an ERROR message.
     *
     * @param msg The message that will be written
     */
    public void error(String msg) {
        this.write(ERROR, msg, this.className);
    }

    /**
     * Log an ERROR message. It can be used to other classes.
     *
     * @param msg The message that will be written
     * @param className The name of class that will be displayed on log message
     */
    public void error(String msg, String className) {
        this.write(ERROR, msg, className);
    }

    public void logStackTrace(@NotNull Exception e) {
        StringBuilder message = new StringBuilder(e.getMessage() + " at:\n");
        // Get the stacktrace that contains the class name
        StackTraceElement[] filteredElement = Arrays.stream(e.getStackTrace())
                .filter((ex) -> ex.toString().contains(this.className))
                .toArray(StackTraceElement[]::new);

        for (StackTraceElement ex : filteredElement)
            message.append(ex.toString()).append("\n");

        this.write(ERROR, message.substring(0, message.lastIndexOf("\n")), this.className);
    }

    /**
     * Get the current line number inside the code.
     *
     * @return int - Current line number.
     */
    private int getLineNumber() {
        return Thread.currentThread()
                .getStackTrace()[DEPTH_LOG_STACK_TRACE]
                .getLineNumber();
    }

    /**
     * @return The location of log file
     */
    public String getPath() {
        return this.path;
    }

}
