import fileDownloaders.HTTPRangeGetter;
import inputHandlers.ProgramInput;
import models.Chunk;
import models.ChunkManager;

import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class DownloadManager {

    private ThreadPoolExecutor threadPool;
    private ArrayList<String> serverList;
    private ChunkManager chunkManager = null;
    private long fileSize;

    /**
     * Creates the download manager object.
     */
    public DownloadManager(ProgramInput userInput) {
        this.serverList = userInput.getServerList();
        this.fileSize = this.getFileSize();

        this.initThreads(userInput.getMaxConnections());
        this.initChunkManager(this.fileSize);
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
     * Initializing the required number of connections and start downloading the file.
     */
    private void initConnections() {
        int chunkCount = this.chunkManager.getChunksCount();

        // todo: add support for different range from different servers
        for (int i = 0; i < 7; i++) { //todo: change loop limit to chunkCount
            HTTPRangeGetter getter =
                    new HTTPRangeGetter(this.serverList.get(0), i, this.chunkManager);
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
     * Kill all existing connection and terminate the download manager execution.
     */
    private void terminateConnections() {
        // maybe ensure download is completed before killing connections?
        this.threadPool.shutdown();
    }

}