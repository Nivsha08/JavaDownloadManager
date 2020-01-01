package models;

import ioHandlers.ProgramPrinter;

public class DownloadStatus {

    private long totalFileSize;
    private long completedBytes = 0;
    private int shownPercentage = 0;

    /**
     * A status object to represent the download status at any given time, and handle the
     * user output regarding the download progress.
     * @param totalFileSize
     */
    public DownloadStatus(long totalFileSize) {
        this.totalFileSize = totalFileSize;
        recoverDownloadStatus();
        ProgramPrinter.printDownloadPercentage(shownPercentage);
    }

    private void recoverDownloadStatus() {
        // todo: implement so this function populates the props by the metadata file
    }

    /**
     * Increments the {@completedBytes} size by the given number.
     * @param completedBytes - amount of completed bytes.
     */
    public void addCompletedBytes(long completedBytes) {
        this.completedBytes += completedBytes;
        updatePercentage();
    }

    /**
     * Updates the progress of the download and present it to the user.
     */
    private void updatePercentage() {
        synchronized (this) {
            double percentage = (double) completedBytes / totalFileSize;
            int newShownPercentage = (int)(percentage * 100);

            if (newShownPercentage > shownPercentage) {
                shownPercentage = newShownPercentage;
                ProgramPrinter.printDownloadPercentage(shownPercentage);
            }
        }
    }

    /**
     * Show a success message when the progress is complete.
     */
    public void handleDownloadSuccess() {
        ProgramPrinter.printSuccessMessage();
    }


}
