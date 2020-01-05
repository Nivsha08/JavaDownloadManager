import java.io.IOException;
import java.net.*;
import java.nio.file.FileSystems;
import java.util.*;
import java.util.concurrent.*;

public class DownloadManager {

    private static final String HEAD_REQUEST_METHOD = "HEAD";

    private String fileName;
    private int numConnections;
    private long fileSize;
    private ArrayList<String> serverList;
    private ThreadPoolExecutor threadPool;
    private MetadataManager metadataManager;
    private ChunkManager chunkManager;
    private ChunkWriter chunkWriter;
    private PriorityBlockingQueue<Chunk> chunkQueue;
    private DownloadStatus downloadStatus;

    /**
     * Creates the download manager object.
     */
    public DownloadManager(ProgramInput userInput) {
        populateProperties(userInput);
        metadataManager = new MetadataManager(fileName, fileSize);
    }

    /**
     * Populate download manager fields by the user input.
     * @param userInput
     */
    private void populateProperties(ProgramInput userInput) {
        serverList = userInput.getServerList();
        numConnections = userInput.getMaxConnections();
        fileName = userInput.getFileName();
        fileSize = getFileSize();
    }

    /**
     * Establish a URL connection to one of the servers and fetch the total
     * desired file size in bytes.
     * @return File size in bytes.
     */
    private long getFileSize() {
        long fileSize = 0;
        try {
            URL url = new URL(this.serverList.get(0));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(HEAD_REQUEST_METHOD);
            fileSize = connection.getContentLength();
            connection.disconnect();
        }
        catch (MalformedURLException e) {
            ProgramPrinter.printError("Invalid URL address given as input.", e);
        }
        catch (IOException e) {
            ProgramPrinter.printError("Unable to get the total size of the source file.", e);
        }

        return fileSize;
    }

    /**
     * Download manager entry point to start the download process.
     */
    public void startDownload() {
        ProgramPrinter.printInitMessage(fileName, serverList.size(), numConnections);
        initThreads(numConnections);
        initDownload();
    }

    /**
     * Initialize a ThreadPool object with the given number of connections.
     * @param n - the required number of connections.
     */
    private void initThreads(int n) {
        this.threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(n);
    }

    /**
     * Initialize the object managing the different download parts.
     */
    private void initDownload() {
        initChunkManager(this.fileSize);
        initDownloadStatus();
        initChunkQueue();
        initChunkWriter();
        initChunkGetters();
        waitForCompletionAndCloseConnections();
    }

    /**
     * Initialize the object managing the download progress and completion status.
     */
    private void initDownloadStatus() {
        long completedBytes = chunkManager.getCompletedChunksIDs().length * Chunk.CHUNK_SIZE;
        if (completedBytes == 0) { // i.e. running the download manager for the first time
            downloadStatus = new DownloadStatus(fileSize);
        }
        else {
            downloadStatus = new DownloadStatus(fileSize, completedBytes);
        }
    }

    /**
     * Creates an array of Chunks objects, initially empty.
     * @param fileSize - the desired file size, in bytes.
     */
    private void initChunkManager(long fileSize) {
        if (metadataManager.isFirstRun()) {
            chunkManager = new ChunkManager(fileSize);
        }
        else {
            chunkManager = metadataManager.load();
        }
    }

    /**
     * Creates a blocking priority queue for handling the completed chunks
     * waiting to be written to file.
     */
    private void initChunkQueue() {
        chunkQueue = new PriorityBlockingQueue<>(chunkManager.getChunksCount());
    }

    /**
     * Initialize a ChunkWriter object to register to the queue.
     */
    private void initChunkWriter() {
        String currentDirPath = FileSystems.getDefault().getPath(".").toAbsolutePath().toString();
        chunkWriter = new ChunkWriter(
                currentDirPath, fileName, chunkQueue, chunkManager, metadataManager, downloadStatus);
        Thread writerThread = new Thread(chunkWriter);
        writerThread.start();
    }

    /**
     * Initializing the required number of connections and start downloading the file.
     */
    private void initChunkGetters() {
        int chunkCount = chunkManager.getChunksCount();
        int[] remainingChunks = chunkManager.getRemainingChunkIDs();

        for (int chunkID : remainingChunks) {
            ChunkRange range = calculateChunkByteRange(chunkID, chunkCount);
            ChunkGetter getter = createGetter(chunkID, range);
            threadPool.execute(getter);
        }
    }

    /**
     * Calculate the range for the current chunk to download from the source file.
     * @param chunkIndex - the chunk's index in the source file.
     * @param chunkCount - the total chunk count.
     * @return ChunkRange object.
     */
    private ChunkRange calculateChunkByteRange(int chunkIndex, int chunkCount) {
        return new ChunkRange(chunkIndex, this.fileSize, chunkCount);
    }

    /**
     * Creates a ChunkGetter object responsible for
     * downloading chunk number <chunkIndex>.
     * @param chunkIndex - the number of chunk to be downloaded from the file.
     * @return a ChunkGetter obejct.
     */
    private ChunkGetter createGetter(int chunkIndex, ChunkRange range) {
        return new ChunkGetter(serverList, range, chunkIndex, chunkManager, chunkQueue);
    }

    /**
     * Kill all existing connection and terminate the download manager execution.
     */
    private void waitForCompletionAndCloseConnections() {
        threadPool.shutdown();
        try {
            threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        }
        catch (InterruptedException e) {
            ProgramPrinter.printError("Some connections were interrupted.", e);
        }
    }

}