package ioHandlers;

/**
 * A static class to handle program messages to the standard error (stderr).
 */
public class ProgramPrinter {

    public static void printDownloadPercentage(int progressPercentage) {
        System.err.println(String.format("Downloaded:\t...\t%d%%", progressPercentage));
    }

    public static void printMessage(String message) {
        System.err.println(message);
    }

    public static void printError(String errorMessage) {
        System.err.println("Download Failed.\n" + errorMessage);
    }

    public static void printError(String errorMessage, Exception e) {
        System.err.println("Download Failed.\n" + errorMessage);
        System.err.println(e);
    }

}
