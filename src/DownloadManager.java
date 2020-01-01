import models.*;
import readers.ChunkGetter;
import writers.ChunkWriter;
import ioHandlers.ProgramInput;
import ioHandlers.ProgramPrinter;

import java.io.IOException;
import java.net.*;
import java.nio.file.FileSystems;
import java.util.*;
import java.util.concurrent.*;

public class DownloadManager {

    private String fileName;
    private int numConnections;
    private long fileSize;
    private ArrayList<String> serverList;
    private ThreadPoolExecutor threadPool;
    private ChunkManager chunkManager;
    private ChunkWriter chunkWriter;
    private ChunkQueue chunkQueue;
    private DownloadStatus downloadStatus;

    /**
     * Creates the download manager object.
     */
    public DownloadManager(ProgramInput userInput) {
        populateProperties(userInput);
    }

    /**
     * Download manager entry point to start the download process.
     */
    public void startDownload() {
        initThreads(numConnections);
        initDownload();
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
     * Initialize the object managing the different download parts.
     */
    private void initDownload() {
        ProgramPrinter.printInitMessage(fileName, serverList.size(), numConnections);
        initDownloadStatus();
        initChunkManager(this.fileSize);
        initChunkQueue();
        initChunkGetters();
        initChunkWriter();
    }

    private void initDownloadStatus() {
        downloadStatus = new DownloadStatus(fileSize);
    }

    /**
     * Initialize a ThreadPool object with the given number of connections.
     * @param n - the required number of connections.
     */
    private void initThreads(int n) {
        this.threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(n);
    }

    /**
     * Creates an array of Chunks objects, initially empty.
     * @param fileSize - the desired file size, in bytes.
     */
    private void initChunkManager(long fileSize) {
        this.chunkManager = new ChunkManager(fileSize, Chunk.CHUNK_SIZE);
    }

    /**
     * Creates a blocking synchronous queue for handling the completed chunks
     * waiting to be written to file.
     */
    private void initChunkQueue() {
        this.chunkQueue = new ChunkQueue(this.chunkManager.getChunksCount());
    }

    /**
     * Initialize a ChunkWriter object to register to the queue.
     */
    private void initChunkWriter() {
        String currentDirPath = FileSystems.getDefault().getPath(".").toAbsolutePath().toString();
        this.chunkWriter = new ChunkWriter(currentDirPath, this.fileName,
                this.chunkQueue, this.downloadStatus);
        Thread writerThread = new Thread(this.chunkWriter);
        writerThread.start();
    }

    /**
     * Initializing the required number of connections and start downloading the file.
     */
    private void initChunkGetters() {
        int chunkCount = this.chunkManager.getChunksCount();

        // todo: add support for different range from different servers
        for (int i = 0; i < chunkCount; i++) {
            ChunkRange range = this.calculateChunkByteRange(i, chunkCount);
            ChunkGetter getter = this.createGetter(i, range);
            this.threadPool.execute(getter);
        }
        this.terminateConnections();
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
     * Establish a URL connection to one of the servers and fetch the total
     * desired file size in bytes.
     * @return File size in bytes.
     */
    private long getFileSize() {
        long fileSize = 0;
        try {
            URL url = new URL(this.serverList.get(0));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
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
     * Creates a ChunkGetter object responsible for
     * downloading chunk number <chunkIndex>.
     * @param chunkIndex - the number of chunk to be downloaded from the file.
     * @return a ChunkGetter obejct.
     */
    private ChunkGetter createGetter(int chunkIndex, ChunkRange range) {
        return new ChunkGetter(
                this.serverList.get(0), range, chunkIndex, this.chunkManager, this.chunkQueue);
    }

    /**
     * Kill all existing connection and terminate the download manager execution.
     */
    private void terminateConnections() {
        // todo: maybe ensure download is completed before killing connections?
        this.threadPool.shutdown();
    }

}