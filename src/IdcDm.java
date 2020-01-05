/**
 * Entry point for the entire program.
 */
public class IdcDm {
    public static void main(String[] args) {
        ProgramInput programInput = UserInputHandler.parseArguments(args);
        DownloadManager downloadManager = new DownloadManager(programInput);
        downloadManager.startDownload();
    }
}
