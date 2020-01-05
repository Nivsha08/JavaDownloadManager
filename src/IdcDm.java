/**
 * Entry point for the entire program.
 */
public class IdcDm {
    /**
     * The download manager program entry point.
     * @param args - terminal arguments.
     */
    public static void main(String[] args) {
        if (args.length < 1 || args.length > 2) {
            handleWrongArgumentsUsage();
        }
        else {
            ProgramInput programInput = UserInputHandler.parseArguments(args);
            DownloadManager downloadManager = new DownloadManager(programInput);
            downloadManager.startDownload();
        }
    }

    /**
     * Show a 'usage: ..." message in case of invalid number of arguments.
     */
    private static void handleWrongArgumentsUsage() {
        ProgramPrinter.printMessage("usage:\n\tjava IdcDm URL | URL-LIST-FILE [MAX-CONCURRENT-CONNECTIONS]");
    }
}
