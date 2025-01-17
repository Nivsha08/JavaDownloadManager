/**
 * A static class to handle program messages to the standard error (stderr).
 */
public class ProgramPrinter {

    private static final String MESSAGE_DIVIDER = "-------------------------------";

    public static void printMessage(String message) {
        System.err.println(message);
    }

    public static void printError(String errorMessage) {
        printMessage(errorMessage);
        printMessage("Download Failed.");
    }

    public static void printError(String errorMessage, Exception e) {
        printMessage(String.format("%s\n%s", errorMessage, e.getLocalizedMessage()));
        printMessage("Download Failed.");
    }

    public static void printDownloadPercentage(int progressPercentage) {
        printMessage(String.format("Downloaded:\t...\t%d%%", progressPercentage));
    }


    public static void printInitMessage(String fileName, int numServers, int numConnections) {
        printMessage(String.format("\nDownloading '%s'\nfrom %d server(s), using %d connection(s).\n%s\n",
                fileName, numServers, numConnections, MESSAGE_DIVIDER));
    }

    public static void printSuccessMessage() {
        printMessage(String.format("%s\nDownload succeeded\n%s",
                MESSAGE_DIVIDER, MESSAGE_DIVIDER));
    }

}
