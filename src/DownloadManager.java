import fileDownloaders.HTTPRangeGetter;
import inputHandlers.ProgramInput;
import models.Chunk;
import models.ChunkManager;
import models.ChunkQueue;

import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class DownloadManager {

    private ThreadPoolExecutor threadPool;
    private ArrayList<String> serverList;
    private ChunkManager chunkManager = null;
    private ChunkQueue chunkQueue = null;
    private long fileSize;

    /**
     * Creates the download manager object.
     */
    public DownloadManager(ProgramInput userInput) {
        this.serverList = userInput.getServerList();
        this.fileSize = this.getFileSize();

        this.initThreads(userInput.getMaxConnections());
        this.initChunkManager(this.fileSize);
        this.initChunkQueue();
        this.initConnections();
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
     * Initializing the required number of connections and start downloading the file.
     */
    private void initConnections() {
        int chunkCount = this.chunkManager.getChunksCount();

        // todo: add support for different range from different servers
        //todo: change loop limit to chunkCount
        for (int i = 0; i < 7; i++) {
            HTTPRangeGetter getter = this.createGetter(i);
            this.threadPool.execute(getter);
        }

        this.terminateConnections();
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
    private HTTPRangeGetter createGetter(int chunkIndex) {
        return new HTTPRangeGetter(
                this.serverList.get(0), chunkIndex, this.chunkManager, this.chunkQueue);
    }

    /**
     * Kill all existing connection and terminate the download manager execution.
     */
    private void terminateConnections() {
        // todo: maybe ensure download is completed before killing connections?
        this.threadPool.shutdown();
    }

}