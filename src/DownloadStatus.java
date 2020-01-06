public class DownloadStatus {

    private long totalFileSize;
    private long totalCompletedBytes = 0;
    private int shownPercentage = 0;
    private boolean isCompleted = false;

    /**
     * Creates a status object to represent the download status at any given time, and handle the
     * user output regarding the download progress.
     * @param totalFileSize - the total file size in bytes.
     */
    public DownloadStatus(long totalFileSize) {
        this.totalFileSize = totalFileSize;
        ProgramPrinter.printDownloadPercentage(shownPercentage);
    }

    /**
     * Creates a object to represent a resumed download status, after completing <completedByes> bytes.
     * @param totalFileSize - the total file size in bytes.
     * @param completedBytes - the total downloaded bytes completed so far.
     */
    public DownloadStatus(long totalFileSize, long completedBytes) {
        this.totalFileSize = totalFileSize;
        this.totalCompletedBytes = completedBytes;
        ProgramPrinter.printMessage("Resuming download...\n");
        updatePercentage();
    }

    /**
     * Increments the {@completedBytes} size by the given number.
     * @param chunkCompletedBytes - amount of completed bytes.
     */
    public void addCompletedBytes(long chunkCompletedBytes) {
        totalCompletedBytes += chunkCompletedBytes;
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

    /**
     * Show a success message when the progress is complete.
     */
    public void handleDownloadSuccess() {
        ProgramPrinter.printSuccessMessage();
    }

    /* GETTERS & SETTERS */

    public boolean isCompleted() {
        return isCompleted;
    }


}
