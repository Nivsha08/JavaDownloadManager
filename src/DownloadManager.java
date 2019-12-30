import fileDownloaders.HTTPRangeGetter;
import fileWriters.ChunkWriter;
import inputHandlers.ProgramInput;
import models.Chunk;
import models.ChunkManager;
import models.ChunkQueue;
import models.ChunkRange;

import java.io.IOException;
import java.net.*;
import java.nio.file.FileSystems;
import java.util.*;
import java.util.concurrent.*;

public class DownloadManager {

    private ThreadPoolExecutor threadPool;
    private ArrayList<String> serverList;
    private ChunkManager chunkManager = null;
    private ChunkWriter chunkWriter = null;
    private ChunkQueue chunkQueue = null;
    private String fileName;
    private long fileSize;

    /**
     * Creates the download manager object.
     */
    public DownloadManager(ProgramInput userInput) {
        this.serverList = userInput.getServerList();
        this.fileSize = this.getFileSize();
        this.fileName = this.getFileName();
        System.out.println(fileName);
        this.initThreads(userInput.getMaxConnections());
        this.initChunkManager(this.fileSize);
        this.initChunkQueue();
        this.initConnections();
        this.initChunkWriter();
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
        this.chunkQueue = new ChunkQueue();
    }

    /**
     * Initialize a ChunkWriter object to register to the queue.
     */
    private void initChunkWriter() {
        String currentDirPath = FileSystems.getDefault().getPath(".").toAbsolutePath().toString();
        this.chunkWriter = new ChunkWriter(currentDirPath, this.fileName, this.chunkQueue);
        Thread writerThread = new Thread(this.chunkWriter);
        writerThread.start();
    }

    /**
     * Initializing the required number of connections and start downloading the file.
     */
    private void initConnections() {
        int chunkCount = this.chunkManager.getChunksCount();

        // todo: add support for different range from different servers
        //todo: change loop limit to chunkCount
        for (int i = 0; i < chunkCount; i++) {
            ChunkRange range = this.calculateChunkByteRange(i, chunkCount);
            HTTPRangeGetter getter = this.createGetter(i, range);
            this.threadPool.execute(getter);
        }
        this.terminateConnections();
    }

    private ChunkRange calculateChunkByteRange(int chunkIndex, int chunkCount) {
        return new ChunkRange(chunkIndex, this.fileSize, chunkCount);
    }

    private String getFileName() {
        int lastBackslashPos = this.serverList.get(0).lastIndexOf('/');
        return this.serverList.get(0).substring(lastBackslashPos + 1);
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
            System.err.println("Download failed.\nInvalid URL address.");
            System.err.println(e);
        }
        catch (IOException e) {
            System.err.println("Download failed.\nUnable to calculate file size.");
            System.err.println(e);
        }

        return fileSize;
    }

    /**
     * Creates a HTTPRangeGetter object responsible for
     * downloading chunk number <chunkIndex>.
     * @param chunkIndex - the number of chunk to be downloaded from the file.
     * @return a HTTPRangeGetter obejct.
     */
    private HTTPRangeGetter createGetter(int chunkIndex, ChunkRange range) {
        return new HTTPRangeGetter(
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