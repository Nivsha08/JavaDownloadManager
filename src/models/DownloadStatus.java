package models;

import ioHandlers.ProgramPrinter;

public class DownloadStatus {

    private long totalFileSize;
    private long totalCompletedBytes = 0;
    private int shownPercentage = 0;
    private boolean isCompleted = false;

    /**
     * A status object to represent the download status at any given time, and handle the
     * user output regarding the download progress.
     * @param totalFileSize
     */
    public DownloadStatus(long totalFileSize) {
        this.totalFileSize = totalFileSize;
        ProgramPrinter.printDownloadPercentage(shownPercentage);
    }

    public DownloadStatus(long totalFileSize, long completedBytes) {
        this.totalFileSize = totalFileSize;
        this.totalCompletedBytes = completedBytes;
        ProgramPrinter.printMessage("Resuming download...\n");
        this.updatePercentage();
    }

    /**
     * Increments the {@completedBytes} size by the given number.
     * @param chunkCompletedBytes - amount of completed bytes.
     */
    public void addCompletedBytes(long chunkCompletedBytes) {
        this.totalCompletedBytes += chunkCompletedBytes;
        updatePercentage();
    }

    /**
     * Updates the progress of the download and present it to the user.
     */
    private void updatePercentage() {
        synchronized (this) {
            double percentage = (double) totalCompletedBytes / totalFileSize;
            int newShownPercentage = (int)(percentage * 100);

            if (newShownPercentage > shownPercentage) {
                shownPercentage = newShownPercentage;
                ProgramPrinter.printDownloadPercentage(shownPercentage);
            }

            if (totalCompletedBytes >= totalFileSize) {
                isCompleted = true;
            }
        }
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    /**
     * Show a success message when the progress is complete.
     */
    public void handleDownloadSuccess() {
        ProgramPrinter.printSuccessMessage();
    }


}
